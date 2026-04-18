package com.github.wechat.ilink.sdk.core.http;

import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.exception.ConnectFailedException;
import com.github.wechat.ilink.sdk.core.retry.RetryPolicy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientFacade {
  private static final Logger log = LoggerFactory.getLogger(HttpClientFacade.class);
  private final OkHttpClient client;
  private final RetryPolicy retryPolicy;

  public HttpClientFacade(ILinkConfig config, RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
    this.client =
        new OkHttpClient.Builder()
            .connectTimeout(config.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
            .readTimeout(config.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
            .writeTimeout(config.getWriteTimeoutMs(), TimeUnit.MILLISECONDS)
            .build();
  }

  public String get(String url, Map<String, String> headers) throws IOException {
    return executeWithRetry("GET", url, headers, null);
  }

  public String post(String url, Map<String, String> headers, String body) throws IOException {
    return executeWithRetry("POST", url, headers, body);
  }

  private String executeWithRetry(
      String method, String url, Map<String, String> headers, String body) throws IOException {
    int attempt = 1;
    while (true) {
      try {
        Request.Builder b = new Request.Builder().url(url);
        if (headers != null)
          for (Map.Entry<String, String> e : headers.entrySet())
            b.addHeader(e.getKey(), e.getValue());
        if ("POST".equals(method))
          b.post(RequestBody.create(body, MediaType.parse("application/json; charset=utf-8")));
        else b.get();
        try (Response r = client.newCall(b.build()).execute()) {
          if (!r.isSuccessful()) throw new IOException(method + " failed code=" + r.code());
          ResponseBody rb = r.body();
          return rb == null ? "" : rb.string();
        }
      } catch (IOException e) {
        if (attempt >= retryPolicy.getMaxAttempts())
          throw new ConnectFailedException(method + " request failed after retries: " + url, e);
        long delay = retryPolicy.nextDelayMillis(attempt);
        log.warn("HTTP retry {} url={} delay={}ms", attempt, url, delay);
        sleep(delay);
        attempt++;
      }
    }
  }

  public String uploadBytes(String url, byte[] data) throws IOException {
    Request req =
        new Request.Builder()
            .url(url)
            .post(RequestBody.create(data, MediaType.parse("application/octet-stream")))
            .build();
    try (Response r = client.newCall(req).execute()) {
      if (!r.isSuccessful()) throw new IOException("upload failed code=" + r.code());
      String encryptedParam = r.header("x-encrypted-param");
      if (encryptedParam == null || encryptedParam.trim().isEmpty())
        throw new IOException("missing x-encrypted-param");
      return encryptedParam;
    }
  }

  public byte[] getBytes(String url) throws IOException {
    Request req = new Request.Builder().url(url).get().build();
    try (Response r = client.newCall(req).execute()) {
      if (!r.isSuccessful()) throw new IOException("download failed code=" + r.code());
      ResponseBody body = r.body();
      return body == null ? new byte[0] : body.bytes();
    }
  }

  private void sleep(long ms) throws IOException {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("interrupted", e);
    }
  }
}
