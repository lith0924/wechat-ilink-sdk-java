# wechat-ilink-sdk

> 企业化设计的微信 iLink Bot Java SDK，支持二维码登录、消息收发、输入态管理、图片/文件/语音/视频发送、媒体下载与会话上下文管理。

---

## 📋 概述

`wechat-ilink-sdk` 是一个面向 Java 开发者的微信 iLink Bot SDK，目标是为业务系统提供一套清晰、稳定、易集成的客户端能力。

当前版本已经覆盖 iLink Bot 的核心主链路：

- 🔐 二维码登录与登录状态轮询
- 📨 长轮询获取消息
- 💬 基于最新 `contextToken` 的文本发送
- 🖼️ 图片、文件、语音、视频发送
- ⌨️ 输入状态开始 / 停止
- ⏳ 带输入态发送文本
- 📥 媒体消息下载与 AES 解密
- 📝 会话上下文缓存与清理
- 🏗️ Builder 模式创建客户端
- ⚙️ 配置化、状态管理、心跳探测、监听器机制

SDK 采用 Builder 模式创建客户端，并内置统一配置、状态管理、异常体系、线程池与资源释放机制，适合在后端服务、机器人程序和集成型项目中使用。

---

## 📦 依赖配置

### Maven

```xml
<dependency>
    <groupId>io.github.lith0924</groupId>
    <artifactId>wechat-ilink-sdk</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.lith0924:wechat-ilink-sdk:2.1.0'
```

---

## ✨ 主要特性

- 🔐 二维码登录与异步登录结果获取
- 📊 登录状态与连接状态管理
- 🏗️ Builder 模式创建客户端
- 👂 登录、消息、心跳监听器
- ❤️ 自动心跳健康探测
- 💬 基于最新 `contextToken` 的消息发送模型
- 📝 文本消息发送
- 🖼️ 图片消息发送
- 📄 文件消息发送
- 🎤 语音消息发送
- 📹 视频消息发送
- ⌨️ 输入状态开始 / 停止控制
- ⏳ 带输入态发送文本
- 📥 媒体下载与 AES 解密
- 📝 会话上下文清理
- ⚙️ 可配置超时、重试、退避与线程池参数
- 🔄 `AutoCloseable` 生命周期支持

---

## 📁 项目结构

### 🏛️ 核心门面

| 组件 | 说明 |
|---|---|
| `ILinkClient` | SDK 主入口，负责登录、消息、输入态、媒体与生命周期管理 |
| `ILinkClientBuilder` | 客户端构建器，用于配置监听器和自定义参数 |

### 🚀 核心服务

| 组件 | 说明 |
|---|---|
| `LoginService` | 处理二维码获取与登录状态轮询 |
| `UpdateService` | 处理 `getUpdates` 长轮询与消息获取 |
| `MessageService` | 处理文本、图片、文件、语音、视频消息发送 |
| `TypingService` | 处理输入状态控制 |
| `MediaService` | 处理媒体上传、下载与 AES 解密 |

### 🔄 状态与上下文

| 组件 | 说明 |
|---|---|
| `LoginStatus` | 登录流程状态 |
| `ConnectionStatus` | 客户端连接状态 |
| `LoginContext` | 登录成功后的凭证与基础信息 |
| `ConversationContext` | 单用户会话上下文，缓存最新 `contextToken` 与 `typingTicket` |

### 🏗️ 基础设施层

| 组件 | 说明 |
|---|---|
| `ILinkConfig` | 客户端配置对象 |
| `ExecutorManager` | 线程池管理 |
| `RetryPolicy` | 重试策略 |
| `HeartbeatService` | 心跳 / 健康探测 |
| `BusinessApiClient` | 业务接口调用封装 |
| `HttpClientFacade` | HTTP 请求封装 |
| `ListenerRegistry` | 监听器注册中心 |

---

## 🚀 快速开始

### 1. 创建客户端

#### 默认配置

```java
ILinkClient client = ILinkClient.builder().build();
```

#### 自定义监听器

```java
ILinkClient client = ILinkClient.builder()
    .onLogin(new OnLoginListener() {
        @Override
        public void onLoginSuccess(LoginContext context) {
            System.out.println("登录成功，botId = " + context.getBotId());
        }

        @Override
        public void onLoginFailure(Throwable throwable) {
            System.err.println("登录失败: " + throwable.getMessage());
        }
    })
    .onMessage(new OnMessageListener() {
        @Override
        public void onMessages(List<WeixinMessage> messages) {
            for (WeixinMessage msg : messages) {
                System.out.println("收到消息，fromUserId = " + msg.getFrom_user_id());
            }
        }
    })
    .build();
```

### 2. 执行登录

```java
String qrCodeContent = client.executeLogin();
System.out.println("请将以下内容渲染为二维码后扫码登录：");
System.out.println(qrCodeContent);

LoginContext context = client.getLoginFuture().get();
System.out.println("登录成功，botId = " + context.getBotId());
```

### 3. 拉取消息

```java
List<WeixinMessage> messages = client.getUpdates();

for (WeixinMessage msg : messages) {
    System.out.println("fromUserId = " + msg.getFrom_user_id());
    System.out.println("contextToken = " + msg.getContext_token());

    if (msg.getItem_list() != null) {
        for (MessageItem item : msg.getItem_list()) {
            if (item.getText_item() != null) {
                System.out.println("text = " + item.getText_item().getText());
            }
        }
    }
}
```

---

## 💬 消息发送

### 发送文本

```java
client.sendText("user@im.wechat", "Hello, iLink!");
```

### 带输入态发送文本

```java
client.sendTextWithTyping("user@im.wechat", "Hello with typing", 1500L);
```

### 发送图片

```java
byte[] imageBytes = Files.readAllBytes(Paths.get("demo.png"));
client.sendImage("user@im.wechat", imageBytes, "demo.png", "这是一张测试图片");
```

### 发送文件

```java
byte[] fileBytes = Files.readAllBytes(Paths.get("demo.pdf"));
client.sendFile("user@im.wechat", fileBytes, "demo.pdf", "这是一个测试文件");
```

### 发送语音

```java
byte[] voiceBytes = Files.readAllBytes(Paths.get("demo.silk"));
client.sendVoice("user@im.wechat", voiceBytes, "demo.silk", 3000, 16000);
```

### 发送视频

```java
byte[] videoBytes = Files.readAllBytes(Paths.get("demo.mp4"));
client.sendVideo("user@im.wechat", videoBytes, "demo.mp4", 5000, "这是一个测试视频");
```

---

## ⌨️ 输入状态管理

### 开始输入态

```java
client.startTyping("user@im.wechat");
```

### 停止输入态

```java
client.stopTyping("user@im.wechat");
```

---

## 📥 媒体下载

### 直接下载媒体

```java
byte[] bytes = client.downloadMedia(cdnMedia);
Files.write(Paths.get("download.bin"), bytes);
```

### 从收到的消息项中下载媒体

```java
List<WeixinMessage> messages = client.getUpdates();

for (WeixinMessage msg : messages) {
    if (msg.getItem_list() == null) {
        continue;
    }

    for (MessageItem item : msg.getItem_list()) {
        if (item.getImage_item() != null
                || item.getFile_item() != null
                || item.getVoice_item() != null
                || item.getVideo_item() != null) {

            byte[] bytes = client.downloadMediaFromMessageItem(item);
            Files.write(Paths.get("download.bin"), bytes);
        }
    }
}
```

### 针对特定类型下载

```java
byte[] imageBytes = client.downloadImageFromMessageItem(item);
byte[] fileBytes  = client.downloadFileFromMessageItem(item);
byte[] voiceBytes = client.downloadVoiceFromMessageItem(item);
byte[] videoBytes = client.downloadVideoFromMessageItem(item);
```

---

## 📜 协议相关概念

这一部分补充 iLink 协议本身的一些核心概念，便于理解 SDK 的设计方式。

### 1. 用户 ID

**普通用户 ID**

```text
格式: xxx@im.wechat
示例: abc123@im.wechat
```

**机器人 ID**

```text
格式: xxx@im.bot
示例: ba36538a1eb2@im.bot
```

### 2. contextToken

`contextToken` 是消息上下文标识，用于将发送消息与对应会话关联起来。

#### 来源

从接收到的消息对象中获取：

```java
String contextToken = msg.getContext_token();
```

#### 用途

发送文本、图片、文件、语音、视频，以及输入态控制时，底层都会依赖这个上下文标识。

#### SDK 中的处理方式

新版 SDK 不要求你手动传入 `contextToken`，而是通过 `getUpdates()` 拉取消息后，自动把最新 `contextToken` 缓存到 `ConversationContext` 中，后续发送时自动使用。

### 3. cursor 游标机制

`cursor` 是消息分页与增量拉取的关键机制。

#### 首次拉取

首次调用时，`cursor` 为空字符串：

```java
List<WeixinMessage> messages = client.getUpdates();
```

SDK 内部会自动管理 `cursor`，业务层通常不需要手动维护。

#### 协议语义

- 新 cursor：获取该 cursor 之后的新消息
- 旧 cursor：可能返回该 cursor 及之后的历史数据
- 首次调用：cursor 为空字符串

新版 SDK 已在 `UpdateService` 内部封装了 `cursor` 存储与更新逻辑。

### 4. client_id

`client_id` 用于消息幂等控制，是每次发送消息时的唯一标识。

#### 作用

- 避免重复发送导致重复接收
- 用于服务端幂等判定

#### SDK 中的处理方式

新版 SDK 内部自动生成 `client_id`，业务层通常不需要手动指定。

### 5. 媒体类型

| 类型值 | 描述 |
|---:|---|
| `1` | 图片 |
| `2` | 视频 |
| `3` | 文件 |
| `4` | 语音 |

新版 SDK 已按该类型映射到图片、视频、文件、语音的上传与发送逻辑。

### 6. 登录状态

| 状态 | 描述 |
|---|---|
| `WAIT` / `WAITING` | 等待扫码 |
| `SCANED` / `SCANNED` | 已扫码 |
| `CONFIRMED` / `LOGGED_IN` | 登录成功 |
| `EXPIRED` | 二维码已过期 |

不同接口与内部状态对象的命名可能略有差异，但语义一致。

---

## 📝 上下文机制说明

SDK 当前采用**缓存最新 `contextToken`** 的发送模型。

### 工作方式

- 当 SDK 通过 `getUpdates()` 拉取到用户消息后，会从入站消息中提取最新的 `contextToken`
- `ConversationContext` 会按 `(botId, userId)` 维度缓存该用户最新上下文
- 后续 `sendText`、`sendImage`、`sendFile`、`sendVoice`、`sendVideo`、`startTyping`、`stopTyping` 等操作会基于该最新 `contextToken` 执行

### 重要前提

发送消息前，目标用户必须先给 bot 发过消息，并且该消息已经被 SDK 通过 `getUpdates()` 拉取到；否则 SDK 无法获取该用户最新 `contextToken`，会抛出上下文缺失异常。

### 清理上下文

```java
client.clearContext("user@im.wechat");
client.clearAllContexts();
```

---

## ⚙️ 配置说明

SDK 通过 `ILinkConfig` 统一管理客户端参数，包括但不限于：

- 连接超时
- 读取超时
- 写入超时
- HTTP 重试次数
- 重试基础退避时间
- 最大退避时间
- 是否启用抖动
- 登录超时
- 心跳间隔
- 是否启用心跳
- 线程池参数
- `channelVersion`
- `routeTag`

### 自定义配置示例

```java
ILinkConfig config = ILinkConfig.builder()
    .connectTimeoutMs(15000)
    .readTimeoutMs(15000)
    .writeTimeoutMs(15000)
    .httpMaxRetries(5)
    .retryBaseDelayMs(1000)
    .retryMaxDelayMs(10000)
    .heartbeatEnabled(true)
    .heartbeatIntervalMs(30000)
    .channelVersion("1.0.0")
    .build();

ILinkClient client = ILinkClient.builder()
    .config(config)
    .build();
```

### 常见配置意义

#### 重试抖动

重试时增加随机抖动的目的，是防止大量请求在同一时刻失败后，又在同一时刻重试，形成“重试风暴”。

#### queueCapacity

`queueCapacity` 表示线程池任务队列容量，用于控制线程繁忙时最多允许多少任务排队等待处理。

---

## 👂 监听器

### 登录监听器

```java
new OnLoginListener() {
    @Override
    public void onLoginSuccess(LoginContext context) {
        // 登录成功
    }

    @Override
    public void onLoginFailure(Throwable throwable) {
        // 登录失败
    }
};
```

### 消息监听器

```java
new OnMessageListener() {
    @Override
    public void onMessages(List<WeixinMessage> messages) {
        // 收到消息
    }
};
```

### 断线监听器

```java
new OnDisconnectListener() {
    @Override
    public void onDisconnect(Throwable throwable) {
        // 断线事件
    }
};
```

### 心跳监听器

```java
new OnHeartbeatListener() {
    @Override
    public void onHeartbeatSuccess() {
        // 心跳成功
    }

    @Override
    public void onHeartbeatFailure(Throwable throwable) {
        // 心跳失败
    }
};
```

---

## ⚠️ 异常处理

```java
try {
    client.sendText("user@im.wechat", "Hello");
} catch (NotLoginException e) {
    System.err.println("请先登录: " + e.getMessage());
} catch (ILinkException e) {
    System.err.println("SDK异常: " + e.getMessage());
} catch (IOException e) {
    System.err.println("网络或IO异常: " + e.getMessage());
}
```

---

## 🔄 生命周期管理

客户端实现了 `AutoCloseable`，建议在业务结束时显式关闭：

```java
client.close();
```

或者：

```java
try (ILinkClient client = ILinkClient.builder().build()) {
    // 使用 client
}
```

---

## 📚 完整示例

```java
import com.github.wechat.ilink.sdk.ILinkClient;
import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.listener.OnLoginListener;
import com.github.wechat.ilink.sdk.core.listener.OnMessageListener;
import com.github.wechat.ilink.sdk.core.login.LoginContext;
import com.github.wechat.ilink.sdk.core.model.MessageItem;
import com.github.wechat.ilink.sdk.core.model.WeixinMessage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class QuickStartExample {

    public static void main(String[] args) throws Exception {
        ILinkConfig config = ILinkConfig.builder()
                .connectTimeoutMs(35000)
                .readTimeoutMs(35000)
                .writeTimeoutMs(35000)
                .httpMaxRetries(3)
                .retryBaseDelayMs(1000)
                .retryMaxDelayMs(10000)
                .heartbeatEnabled(true)
                .heartbeatIntervalMs(30000)
                .channelVersion("1.0.0")
                .build();

        ILinkClient client = ILinkClient.builder()
                .config(config)
                .onLogin(new OnLoginListener() {
                    @Override
                    public void onLoginSuccess(LoginContext context) {
                        System.out.println("登录成功，botId = " + context.getBotId());
                    }

                    @Override
                    public void onLoginFailure(Throwable throwable) {
                        System.err.println("登录失败: " + throwable.getMessage());
                    }
                })
                .onMessage(new OnMessageListener() {
                    @Override
                    public void onMessages(List<WeixinMessage> messages) {
                        for (WeixinMessage msg : messages) {
                            System.out.println("收到消息 fromUserId = " + msg.getFrom_user_id());
                            if (msg.getItem_list() != null) {
                                for (MessageItem item : msg.getItem_list()) {
                                    if (item.getText_item() != null) {
                                        System.out.println("text = " + item.getText_item().getText());
                                    }
                                }
                            }
                        }
                    }
                })
                .build();

        try {
            String qrCodeContent = client.executeLogin();
            System.out.println("请扫码登录：");
            System.out.println(qrCodeContent);

            LoginContext context = client.getLoginFuture().get();
            System.out.println("登录完成，botId = " + context.getBotId());

            List<WeixinMessage> messages = client.getUpdates();
            System.out.println("首次拉取消息数 = " + messages.size());

            String targetUserId = "这里替换成真实的 from_user_id";

            client.sendText(targetUserId, "Hello, iLink!");
            client.sendTextWithTyping(targetUserId, "这是一条带输入态的消息", 1500L);

            byte[] imageBytes = Files.readAllBytes(Paths.get("demo.png"));
            client.sendImage(targetUserId, imageBytes, "demo.png", "这是一张测试图片");

            byte[] fileBytes = Files.readAllBytes(Paths.get("demo.pdf"));
            client.sendFile(targetUserId, fileBytes, "demo.pdf", "这是一个测试文件");

            byte[] voiceBytes = Files.readAllBytes(Paths.get("demo.silk"));
            client.sendVoice(targetUserId, voiceBytes, "demo.silk", 3000, 16000);

            byte[] videoBytes = Files.readAllBytes(Paths.get("demo.mp4"));
            client.sendVideo(targetUserId, videoBytes, "demo.mp4", 5000, "这是一个测试视频");

        } finally {
            client.close();
        }
    }
}
```

---

## ❓ 常见问题

### 1. 登录失败或登录结果为空
优先检查：

- 网络是否正常
- 二维码是否过期
- 登录轮询是否完整结束
- 是否成功拿到 `LoginContext`

### 2. 发送消息时报 `missing latest context token`
原因通常是目标用户还没有被 `getUpdates()` 拉到最新上下文。  
解决方式：

1. 先让该用户给 bot 发消息  
2. 调用 `getUpdates()`  
3. 再执行发送

### 3. 媒体发送失败
优先检查：

- 文件是否能正常读取
- `getuploadurl` 是否成功返回
- CDN 上传是否成功
- `contextToken` 是否存在

### 4. 媒体下载失败
优先检查：

- 收到的消息项里是否真的包含媒体
- `CDNMedia.encrypt_query_param` 是否存在
- `CDNMedia.aes_key` 是否存在
- 下载后的内容是否被正确保存

### 5. 登录掉线后如何恢复
当前产品模型下，登录状态失效后需要**手动重新登录**，不支持自动重新拉起二维码登录流程。

---

## 🛣️ 路线说明

当前版本已经覆盖核心 SDK 主链路。后续如需增强，可考虑：

- 更智能的媒体文件名与扩展名识别
- 更丰富的示例代码
- 更完整的单元测试与集成测试
- 更细粒度的日志与观测能力

---

## 📄 许可证

MIT License

---

## 🤝 支持与反馈

- GitHub Issues：用于提交 Bug 与使用问题
- Pull Request：欢迎贡献文档、示例和代码改进
