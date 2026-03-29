package com.lth.wechat.ilink.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

/**
 * 媒体处理工具类
 * 提供AES加密/解密、文件处理、CDN上传/下载等功能
 */
public class MediaUtils {
    private static final Logger logger = LoggerFactory.getLogger(MediaUtils.class);
    public static final String CDN_BASE_URL = "https://novac2c.cdn.weixin.qq.com/c2c";
    
    /**
     * AES-128-ECB加密
     */
    public static byte[] encryptAesEcb(byte[] plaintext, byte[] key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(plaintext);
    }
    
    /**
     * AES-128-ECB解密
     */
    public static byte[] decryptAesEcb(byte[] ciphertext, byte[] key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(ciphertext);
    }
    
    /**
     * 计算文件MD5
     */
    public static String calculateMd5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(data);
        return HexFormat.of().formatHex(digest);
    }
    
    /**
     * 计算密文大小
     */
    public static long calculateEncryptedSize(long rawSize) {
        return ((rawSize + 15) / 16) * 16;
    }
    
    /**
     * 生成随机filekey
     */
    public static String generateFileKey() {
        byte[] randomBytes = new byte[16];
        java.security.SecureRandom random = new java.security.SecureRandom();
        random.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }
    
    /**
     * 生成随机AES key
     */
    public static byte[] generateAesKey() {
        byte[] key = new byte[16];
        java.security.SecureRandom random = new java.security.SecureRandom();
        random.nextBytes(key);
        return key;
    }
    
    /**
     * AES key转hex字符串
     */
    public static String aesKeyToHex(byte[] key) {
        return HexFormat.of().formatHex(key);
    }
    
    /**
     * Hex字符串转AES key
     */
    public static byte[] hexToAesKey(String hex) {
        return HexFormat.of().parseHex(hex);
    }
    
    /**
     * 处理AES key的两种编码格式
     */
    public static byte[] processAesKey(String aesKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(aesKey);
        if (decoded.length == 16) {
            return decoded;
        } else if (decoded.length == 32) {
            String hexStr = new String(decoded);
            if (hexStr.matches("^[0-9a-fA-F]{32}$")) {
                return HexFormat.of().parseHex(hexStr);
            }
        }
        throw new Exception("Invalid AES key format");
    }
    
    /**
     * 上传文件到CDN
     */
    public static String uploadToCdn(byte[] encryptedData, String uploadParam, String fileKey) throws IOException {
        String encodedUploadParam = java.net.URLEncoder.encode(uploadParam, "UTF-8");
        String encodedFileKey = java.net.URLEncoder.encode(fileKey, "UTF-8");
        String url = CDN_BASE_URL + "/upload?encrypted_query_param=" + encodedUploadParam + "&filekey=" + encodedFileKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(encryptedData);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String encryptedParam = conn.getHeaderField("x-encrypted-param");
                if (encryptedParam == null || encryptedParam.isEmpty()) {
                    logger.debug("CDN upload response headers:");
                    for (int i = 0; ; i++) {
                        String headerName = conn.getHeaderFieldKey(i);
                        String headerValue = conn.getHeaderField(i);
                        if (headerName == null && headerValue == null) {
                            break;
                        }
                        logger.debug("{}: {}", headerName, headerValue);
                    }
                    throw new IOException("x-encrypted-param header not found");
                }
                return encryptedParam;
            } else {
                throw new IOException("CDN upload failed with code: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * 从CDN下载文件
     */
    public static byte[] downloadFromCdn(String encryptQueryParam) throws IOException {
        logger.debug("开始从CDN下载文件");
        logger.debug("原始encryptQueryParam: {}", encryptQueryParam);
        
        // 尝试对参数进行URL编码，因为根据错误信息，参数可能需要URL编码
        String encodedParam = java.net.URLEncoder.encode(encryptQueryParam, "UTF-8");
        logger.debug("URL编码后的参数: {}", encodedParam);
        
        String url = CDN_BASE_URL + "/download?encrypted_query_param=" + encodedParam;
        logger.debug("下载URL: {}", url);
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            logger.debug("发送GET请求...");
            int responseCode = conn.getResponseCode();
            logger.debug("响应码: {}", responseCode);
            
            // 打印响应头
            logger.debug("响应头:");
            for (int i = 0; ; i++) {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);
                if (headerName == null && headerValue == null) {
                    break;
                }
                logger.debug("{}: {}", headerName, headerValue);
            }
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = conn.getInputStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byte[] result = baos.toByteArray();
                    logger.debug("下载成功，文件大小: {} bytes", result.length);
                    return result;
                }
            } else {
                // 读取错误响应
                try (InputStream errorStream = conn.getErrorStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    if (errorStream != null) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = errorStream.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        String errorResponse = new String(baos.toByteArray());
                        logger.debug("错误响应: {}", errorResponse);
                    }
                } catch (Exception e) {
                    logger.debug("读取错误响应失败: {}", e.getMessage());
                }
                throw new IOException("CDN download failed with code: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
}