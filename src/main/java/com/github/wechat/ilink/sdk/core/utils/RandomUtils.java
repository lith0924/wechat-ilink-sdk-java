package com.github.wechat.ilink.sdk.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public final class RandomUtils {
  private static final SecureRandom R = new SecureRandom();
  private static final SecureRandom RANDOM = new SecureRandom();

  private RandomUtils() {}

  public static String randomWechatUin() {
    long v = R.nextInt() & 0xffffffffL;
    return Base64.getEncoder().encodeToString(String.valueOf(v).getBytes(StandardCharsets.UTF_8));
  }

  public static String clientId(String prefix) {
    return prefix
        + ":"
        + System.currentTimeMillis()
        + "-"
        + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  public static String randomHex(int bytes) {
    byte[] buf = new byte[bytes];
    RANDOM.nextBytes(buf);
    StringBuilder sb = new StringBuilder(bytes * 2);
    for (byte b : buf) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }
}
