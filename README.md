# wechat-ilink-sdk-java

> 微信 iLink Bot Java SDK，支持二维码登录、消息收发、媒体上传与AES加解密。

---

## 📦 Maven 依赖

```xml
<dependency>
    <groupId>暂未发布</groupId>
    <artifactId>暂未发布</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 📁 项目结构

### API 分类

| 类型 | 说明 | 示例 |
|:----:|:----|:----|
| **原始API** | iLink 官方提供的底层接口 | `getBotQrCode()`, `getQrCodeStatus()` |
| **封装API** | 封装后的用户友好接口 | `getQrCodeInfo()`, `pollLoginStatus()` |
| **DTO 类** | 封装后的用户友好数据结构 | `QrCodeInfo`, `LoginStatus`, `ReceiveMessagesResult` |
| **实体类** | 原始API响应结构 | `QrCodeResp`, `LoginStatusResp`, `GetUpdatesResponse` |

---

## 🚀 快速开始

```java
// 1. 创建客户端
ILinkClient client = new ILinkClient();

// 2. 获取二维码（封装API）
QrCodeInfo qrCode = client.getQrCodeInfo();
System.out.println("请扫码: " + qrCode.getImageUrl());
System.out.println("二维码ID: " + qrCode.getQrcodeId());

// 3. 轮询登录状态（封装API）
LoginStatus status;
do {
    Thread.sleep(3000);
    status = client.pollLoginStatus(qrCode.getQrcodeId());
    System.out.println("登录状态: " + status.getStatus());
} while (!status.isSuccess());

// 4. 创建登录凭证（封装类）
LoginCredentials credentials = new LoginCredentials(
    status.getToken(),  // bot token
    status.getUserId(), // 用户ID
    status.getApiBaseUrl() // API基础URL
);

// 5. 接收消息（封装API）
ReceiveMessagesResult result = client.receiveMessages(credentials, "");
result.getMessages().forEach(msg -> {
    // 消息内容在 item_list 中
    msg.getItemList().forEach(item -> {
        System.out.println("收到: " + item.getText());  // 文本内容
    });
    System.out.println("发送者: " + msg.getFromUserId());
    System.out.println("消息类型: " + msg.getType());
});

// 6. 发送文本消息（封装API）
client.sendTextMessage(
    credentials,      // 登录凭证
    "abc123@im.wechat",  // 接收者ID
    "context_token",  // 上下文token
    "Hello, iLink!"    // 消息内容
);

// 7. 发送图片（需先上传媒体）
byte[] imageBytes = Files.readAllBytes(Paths.get("image.jpg"));
MediaInfo media = client.uploadMedia(
    credentials,  // 登录凭证
    1,            // 媒体类型：1=图片
    "abc123@im.wechat", // 接收者ID
    imageBytes    // 图片数据
);

client.sendImageMessage(
    credentials,  // 登录凭证
    "abc123@im.wechat", // 接收者ID
    "context_token", // 上下文token
    media         // 媒体信息
);
```

---

## 📚 核心API

### 🔐 登录相关

| 方法 | 参数 | 返回 | 说明 | 类型 |
|:----|:----|:----|:----|:----|
| `getQrCodeInfo()` | - | `QrCodeInfo` | 获取登录二维码 | 封装API |
| `pollLoginStatus(qrcode)` | qrcode: 二维码ID | `LoginStatus` | 轮询登录状态 | 封装API |
| `getBotQrCode()` | - | `QrCodeResp` | 获取原始二维码响应 | 原始API |
| `getQrCodeStatus(qrcode)` | qrcode: 二维码ID | `LoginStatusResp` | 获取原始登录状态 | 原始API |

### 💬 消息收发

| 方法 | 参数 | 返回 | 说明 | 类型 |
|:----|:----|:----|:----|:----|
| `receiveMessages(credentials, cursor)` | credentials: 登录凭证<br>cursor: 消息游标 | `ReceiveMessagesResult` | 接收消息 | 封装API |
| `sendTextMessage(credentials, toUserId, contextToken, text)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>contextToken: 上下文token<br>text: 消息内容 | void | 发送文本 | 封装API |
| `sendImageMessage(credentials, toUserId, contextToken, mediaInfo)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>contextToken: 上下文token<br>mediaInfo: 媒体信息 | void | 发送图片 | 封装API |
| `sendVideoMessage(credentials, toUserId, contextToken, mediaInfo, videoSize, duration)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>contextToken: 上下文token<br>mediaInfo: 媒体信息<br>videoSize: 视频大小<br>duration: 视频时长 | void | 发送视频 | 封装API |
| `sendFileMessage(credentials, toUserId, contextToken, mediaInfo, fileName, fileSize)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>contextToken: 上下文token<br>mediaInfo: 媒体信息<br>fileName: 文件名<br>fileSize: 文件大小 | void | 发送文件 | 封装API |
| `sendVoiceMessage(credentials, toUserId, contextToken, mediaInfo, duration, encodeType)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>contextToken: 上下文token<br>mediaInfo: 媒体信息<br>duration: 语音时长<br>encodeType: 编码类型 | void | 发送语音 | 封装API |
| `sendReplyMessage(credentials, toUserId, contextToken, refMessage, replyText)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>contextToken: 上下文token<br>refMessage: 引用消息<br>replyText: 回复内容 | void | 发送引用回复 | 封装API |
| `sendTypingStatus(credentials, toUserId, typingTicket, isTyping)` | credentials: 登录凭证<br>toUserId: 接收者ID<br>typingTicket: 输入凭证<br>isTyping: 是否正在输入 | void | 发送输入状态 | 封装API |
| `getUpdates(credentials, cursor)` | credentials: 登录凭证<br>cursor: 消息游标 | `GetUpdatesResponse` | 原始消息更新 | 原始API |
| `sendMessage(credentials, request)` | credentials: 登录凭证<br>request: 发送消息请求 | `SendMessageResp` | 原始发送消息 | 原始API |

### 📤 媒体上传

| 方法 | 参数 | 返回 | 说明 | 类型 |
|:----|:----|:----|:----|:----|
| `uploadMedia(credentials, mediaType, toUserId, fileData)` | credentials: 登录凭证<br>mediaType: 媒体类型<br>toUserId: 接收者ID<br>fileData: 文件数据 | `MediaInfo` | 上传媒体文件 | 封装API |
| `getUploadUrl(credentials, req)` | credentials: 登录凭证<br>req: 获取上传URL请求 | `GetUploadUrlResp` | 获取上传URL | 原始API |

### ⚙️ 配置相关

| 方法 | 参数 | 返回 | 说明 | 类型 |
|:----|:----|:----|:----|:----|
| `getConfig(credentials, request)` | credentials: 登录凭证<br>request: 配置请求 | `GetConfigResp` | 获取配置 | 原始API |
| `sendTyping(credentials, request)` | credentials: 登录凭证<br>request: typing请求 | `SendTypingResp` | 发送typing | 原始API |

---

## 📖 参数说明

### 核心参数

| 参数 | 类型 | 说明 | 来源 |
|:----|:----|:----|:----|
| `credentials` | `LoginCredentials` | 登录凭证，包含token、用户ID等 | 登录成功后生成 |
| `qrcode` | `String` | 二维码唯一标识，用于轮询登录状态 | `QrCodeInfo.getQrcodeId()` |
| `toUserId` | `String` | 消息接收者ID，格式：`xxx@im.wechat` | `msg.getFromUserId()` |
| `contextToken` | `String` | 上下文token，用于消息关联 | `msg.getContextToken()` |
| `cursor` | `String` | 消息游标，用于分页获取消息 | `result.getNextCursor()` |
| `mediaType` | `int` | 媒体类型：1=图片, 2=视频, 3=文件, 4=语音 | 固定值 |
| `mediaInfo` | `MediaInfo` | 媒体信息，上传媒体后返回 | `uploadMedia()`返回 |

### 用户ID格式

**普通用户ID**
```
格式: xxx@im.wechat
示例: abc123@im.wechat
```

**机器人ID**
```
格式: xxx@im.bot
示例: ba36538a1eb2@im.bot
```

### Client ID

- **作用**: 消息唯一标识，用于幂等控制
- **生成**: 每次随机生成
- **特性**: 相同的 client_id 会幂等（重复发送不会重复接收）

### Context Token

- **作用**: 消息的上下文标识，用于关联发送的消息
- **来源**: 从接收到的消息对象中获取 `msg.getContextToken()`
- **用途**: 发送消息时传入，用于绑定上下文关系
- **限制**: 每个 context token 最多只能被回复 10 次

### Cursor 游标机制

**重要**: cursor 是消息分页的关键机制

```java
// 首次获取消息，cursor 传空字符串
ReceiveMessagesResult result = client.receiveMessages(credentials, "");
String nextCursor = result.getNextCursor();

// 下次使用新的 cursor 获取后续消息
// 注意：使用旧 cursor 会返回该 cursor 及之后所有数据
ReceiveMessagesResult nextResult = client.receiveMessages(credentials, nextCursor);
```

**规则**:
- 新 cursor → 获取该 cursor 之后的新消息
- 旧 cursor → 返回该 cursor 及之后的所有历史数据
- 首次调用 → cursor 传空字符串 `""`

### 媒体类型

| 类型值 | 描述 |
|:----:|:----|
| 1 | 图片 |
| 2 | 视频 |
| 3 | 文件 |
| 4 | 语音 |

### 登录状态

| 状态 | 描述 |
|:----|:----|
| `WAIT` | 等待扫码 |
| `SCANED` | 已扫码 |
| `CONFIRMED` | 已确认（登录成功） |
| `EXPIRED` | 已过期 |

---

## ⚠️ 异常处理

```java
try {
    ReceiveMessagesResult result = client.receiveMessages(credentials, cursor);
    // 处理消息
} catch (ILinkSessionExpiredException e) {
    // 会话过期（ret=-14），需重新登录
    System.out.println("会话已过期，请重新扫码登录");
} catch (ILinkException e) {
    // 其他SDK错误
    System.err.println("错误: " + e.getMessage());
}
```

---

## ⚙️ 自定义配置

```java
// 自定义HttpClient配置
HttpClient httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))  // 连接超时
    .build();

ILinkClient client = new ILinkClient(httpClient);
```

---

## ✨ 内置特性

- ✅ **会话过期检测** - 自动检测 ret=-14 错误，抛出 `ILinkSessionExpiredException`
- ✅ **重试机制** - 网络波动时自动重试，采用指数退避策略
- ✅ **资源管理** - 自动管理HTTP连接资源，防止内存泄漏
- ✅ **日志支持** - 使用 SLF4J 日志框架，便于调试和监控
