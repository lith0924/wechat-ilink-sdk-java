package com.github.wechat.ilink.sdk;

import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.context.ContextPoolManager;
import com.github.wechat.ilink.sdk.core.context.GetUpdatesCursorStore;
import com.github.wechat.ilink.sdk.core.context.ResumeContext;
import com.github.wechat.ilink.sdk.core.exception.ILinkException;
import com.github.wechat.ilink.sdk.core.exception.NotLoginException;
import com.github.wechat.ilink.sdk.core.executor.ExecutorManager;
import com.github.wechat.ilink.sdk.core.http.BusinessApiClient;
import com.github.wechat.ilink.sdk.core.http.HttpClientFacade;
import com.github.wechat.ilink.sdk.core.lifecycle.HealthChecker;
import com.github.wechat.ilink.sdk.core.lifecycle.HeartbeatService;
import com.github.wechat.ilink.sdk.core.listener.ListenerRegistry;
import com.github.wechat.ilink.sdk.core.listener.OnLoginListener;
import com.github.wechat.ilink.sdk.core.listener.OnMessageListener;
import com.github.wechat.ilink.sdk.core.login.LoginContext;
import com.github.wechat.ilink.sdk.core.login.LoginStatus;
import com.github.wechat.ilink.sdk.core.login.QRCodeResponse;
import com.github.wechat.ilink.sdk.core.model.CDNMedia;
import com.github.wechat.ilink.sdk.core.model.FileItem;
import com.github.wechat.ilink.sdk.core.model.ImageItem;
import com.github.wechat.ilink.sdk.core.model.MessageItem;
import com.github.wechat.ilink.sdk.core.model.VideoItem;
import com.github.wechat.ilink.sdk.core.model.VoiceItem;
import com.github.wechat.ilink.sdk.core.retry.ExponentialBackoffStrategy;
import com.github.wechat.ilink.sdk.core.retry.RetryPolicy;
import com.github.wechat.ilink.sdk.core.serializer.JsonSerializer;
import com.github.wechat.ilink.sdk.core.serializer.Serializer;
import com.github.wechat.ilink.sdk.core.state.ClientStateManager;
import com.github.wechat.ilink.sdk.core.state.ConnectionStatus;
import com.github.wechat.ilink.sdk.service.LoginService;
import com.github.wechat.ilink.sdk.service.MediaService;
import com.github.wechat.ilink.sdk.service.MessageService;
import com.github.wechat.ilink.sdk.service.TypingService;
import com.github.wechat.ilink.sdk.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ILinkClient implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ILinkClient.class);

    private final ILinkConfig config;
    private final ListenerRegistry listenerRegistry;
    private final ExecutorManager executorManager;
    private final ClientStateManager stateManager = new ClientStateManager();
    private final ContextPoolManager contextPoolManager = new ContextPoolManager();
    private final GetUpdatesCursorStore cursorStore = new GetUpdatesCursorStore();

    private final Serializer serializer;
    private final RetryPolicy retryPolicy;
    private final HttpClientFacade httpClientFacade;
    private final BusinessApiClient businessApiClient;

    private final LoginService loginService;
    private final LoginStatus loginStatus = new LoginStatus();
    private final AtomicReference<LoginContext> loginContext = new AtomicReference<LoginContext>();
    private final UpdateService updateService;
    private final MediaService mediaService;
    private final MessageService messageService;
    private final TypingService typingService;

    private volatile CompletableFuture<LoginContext> loginFuture;
    private volatile String qrcode;
    private HeartbeatService heartbeatService;

    public static ILinkClientBuilder builder() {
        return new ILinkClientBuilder();
    }

    public ILinkClient(ILinkConfig config, ListenerRegistry listenerRegistry, ResumeContext resumeContext) {
        this.config = config;
        this.listenerRegistry = listenerRegistry;
        this.executorManager = new ExecutorManager(config);
        this.serializer = new JsonSerializer();
        this.retryPolicy =
            new RetryPolicy(
                config.getHttpMaxRetries(),
                new ExponentialBackoffStrategy(
                    config.getRetryBaseDelayMs(),
                    config.getRetryMaxDelayMs(),
                    config.isRetryJitterEnabled()));
        this.httpClientFacade = new HttpClientFacade(config, retryPolicy);
        this.businessApiClient = new BusinessApiClient(config, serializer, httpClientFacade);
        this.loginService =
            new LoginService(config, serializer, httpClientFacade, executorManager.ioExecutor());
        this.updateService =
            new UpdateService(config, businessApiClient, cursorStore, contextPoolManager);
        this.mediaService = new MediaService(config, businessApiClient, httpClientFacade);
        this.messageService =
            new MessageService(config, businessApiClient, mediaService, contextPoolManager);
        this.typingService = new TypingService(config, businessApiClient, contextPoolManager);
        if (resumeContext != null && resumeContext.getLoginContext() != null) {
            this.loginContext.set(resumeContext.getLoginContext());
            loginStatus.toLoggedIn();
            stateManager.set(ConnectionStatus.LOGGED_IN);
            if (resumeContext.getUpdatesCursor() != null) {
                cursorStore.put(resumeContext.getUpdatesCursor());
            }
            contextPoolManager.restore(resumeContext.getConversationContexts());
        }
        initHeartbeat();
        if (stateManager.isLoggedIn() && heartbeatService != null) {
            heartbeatService.start();
        }
    }

    private void initHeartbeat() {
        if (!config.isHeartbeatEnabled()) return;
        this.heartbeatService =
            new HeartbeatService(
                executorManager.scheduler(),
                config.getHeartbeatIntervalMs(),
                    () -> {
                        if (!stateManager.isLoggedIn()) return;
                        LoginContext ctx = loginContext.get();
                        if (ctx == null) return;
                        updateService.poll(ctx);
                    },
                listenerRegistry);
    }

    public String executeLogin() {
        stateManager.set(ConnectionStatus.CONNECTING);
        try {
            QRCodeResponse response = loginService.getQRCode();
            this.qrcode = response.getQrcode();
            this.loginFuture = loginService.startLoginPolling(qrcode, loginStatus, loginContext);
            this.loginFuture.whenComplete(
                (ctx, ex) -> {
                    if (ex != null) {
                        stateManager.set(ConnectionStatus.DISCONNECTED);
                        for (OnLoginListener l : listenerRegistry.getLoginListeners()) {
                            l.onLoginFailure(ex);
                        }
                        return;
                    }
                    stateManager.set(ConnectionStatus.LOGGED_IN);
                    for (OnLoginListener l : listenerRegistry.getLoginListeners()) {
                        l.onLoginSuccess(ctx);
                    }
                    if (heartbeatService != null) {
                        heartbeatService.start();
                    }
                });
            return response.getQrcodeImgContent();
        } catch (RuntimeException | IOException e) {
            stateManager.set(ConnectionStatus.DISCONNECTED);
            for (OnLoginListener l : listenerRegistry.getLoginListeners()) {
                l.onLoginFailure(e);
            }
            throw new RuntimeException("start login failed", e);
        }
    }

    public List<com.github.wechat.ilink.sdk.core.model.WeixinMessage> getUpdates()
        throws IOException {
        List<com.github.wechat.ilink.sdk.core.model.WeixinMessage> messages =
            updateService.poll(requireLogin());
        if (messages != null && !messages.isEmpty()) {
            for (OnMessageListener l : listenerRegistry.getMessageListeners()) {
                l.onMessages(messages);
            }
        }
        return messages;
    }

    public void sendText(String toUserId, String text) throws IOException {
        messageService.sendText(requireLogin(), toUserId, text);
    }

    public void sendTextWithTyping(String toUserId, String text, long typingMillis)
        throws IOException {
        typingService.startTyping(requireLogin(), toUserId);
        try {
            if (typingMillis > 0) {
                try {
                    Thread.sleep(typingMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            messageService.sendText(requireLogin(), toUserId, text);
        } finally {
            typingService.stopTyping(requireLogin(), toUserId);
        }
    }

    public void sendImage(String toUserId, byte[] imageBytes, String fileName, String caption)
        throws IOException {
        messageService.sendImage(requireLogin(), toUserId, imageBytes, fileName, caption);
    }

    public void sendFile(String toUserId, byte[] fileBytes, String fileName, String caption)
        throws IOException {
        messageService.sendFile(requireLogin(), toUserId, fileBytes, fileName, caption);
    }

    public void sendVoice(
        String toUserId, byte[] voiceBytes, String fileName, Integer playTimeMs, Integer sampleRate)
        throws IOException {
        messageService.sendVoice(
            requireLogin(), toUserId, voiceBytes, fileName, playTimeMs, sampleRate);
    }

    public void sendVideo(
        String toUserId, byte[] videoBytes, String fileName, Integer playLengthMs, String caption)
        throws IOException {
        messageService.sendVideo(
            requireLogin(), toUserId, videoBytes, fileName, playLengthMs, caption);
    }

    public void startTyping(String toUserId) throws IOException {
        typingService.startTyping(requireLogin(), toUserId);
    }

    public void stopTyping(String toUserId) throws IOException {
        typingService.stopTyping(requireLogin(), toUserId);
    }

    public byte[] downloadMedia(CDNMedia media) throws IOException {
        return mediaService.downloadMedia(media);
    }

    public byte[] downloadMediaFromMessageItem(MessageItem item) throws IOException {
        if (item == null) {
            throw new ILinkException("message item is null");
        }

        ImageItem imageItem = item.getImage_item();
        if (imageItem != null && imageItem.getMedia() != null) {
            return mediaService.downloadMedia(imageItem.getMedia());
        }

        FileItem fileItem = item.getFile_item();
        if (fileItem != null && fileItem.getMedia() != null) {
            return mediaService.downloadMedia(fileItem.getMedia());
        }

        VoiceItem voiceItem = item.getVoice_item();
        if (voiceItem != null && voiceItem.getMedia() != null) {
            return mediaService.downloadMedia(voiceItem.getMedia());
        }

        VideoItem videoItem = item.getVideo_item();
        if (videoItem != null && videoItem.getMedia() != null) {
            return mediaService.downloadMedia(videoItem.getMedia());
        }

        throw new ILinkException("message item does not contain downloadable media");
    }

    public byte[] downloadImageFromMessageItem(MessageItem item) throws IOException {
        if (item == null || item.getImage_item() == null || item.getImage_item().getMedia() == null) {
            throw new ILinkException("message item does not contain image media");
        }
        return mediaService.downloadMedia(item.getImage_item().getMedia());
    }

    public byte[] downloadFileFromMessageItem(MessageItem item) throws IOException {
        if (item == null || item.getFile_item() == null || item.getFile_item().getMedia() == null) {
            throw new ILinkException("message item does not contain file media");
        }
        return mediaService.downloadMedia(item.getFile_item().getMedia());
    }

    public byte[] downloadVoiceFromMessageItem(MessageItem item) throws IOException {
        if (item == null || item.getVoice_item() == null || item.getVoice_item().getMedia() == null) {
            throw new ILinkException("message item does not contain voice media");
        }
        return mediaService.downloadMedia(item.getVoice_item().getMedia());
    }

    public byte[] downloadVideoFromMessageItem(MessageItem item) throws IOException {
        if (item == null || item.getVideo_item() == null || item.getVideo_item().getMedia() == null) {
            throw new ILinkException("message item does not contain video media");
        }
        return mediaService.downloadMedia(item.getVideo_item().getMedia());
    }

    public CompletableFuture<LoginContext> getLoginFuture() {
        return loginFuture;
    }

    public LoginContext getLoginContext() {
        return loginContext.get();
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public ConnectionStatus getConnectionStatus() {
        return stateManager.get();
    }

    public boolean isLoggedIn() {
        return stateManager.isLoggedIn();
    }

    public String getQrcode() {
        return qrcode;
    }

    public ILinkConfig getConfig() {
        return config;
    }

    public void clearContext(String userId) {
        LoginContext ctx = loginContext.get();
        if (ctx != null) {
            contextPoolManager.remove(ctx.getBotId(), userId);
        }
    }

    public void clearAllContexts() {
        contextPoolManager.clearAll();
    }

    public ResumeContext exportResumeContext() {
        LoginContext ctx = loginContext.get();
        if (ctx == null) {
            return null;
        }
        return ResumeContext.builder(ctx)
            .updatesCursor(cursorStore.get())
            .conversationContexts(contextPoolManager.snapshotByUserId())
            .build();
    }

    public void cancelLogin() {
        loginService.cancelCurrentLogin();
    }

    private LoginContext requireLogin() {
        LoginContext ctx = loginContext.get();
        if (ctx == null) {
            throw new NotLoginException("not logged in");
        }
        return ctx;
    }

    @Override
    public void close() {
        log.info("closing ILinkClient");
        stateManager.set(ConnectionStatus.DISCONNECTING);
        if (heartbeatService != null) {
            heartbeatService.close();
        }
        loginService.close();
        cursorStore.clear();
        contextPoolManager.clearAll();
        executorManager.close();
        stateManager.set(ConnectionStatus.CLOSED);
    }
}
