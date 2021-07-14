package org.etocrm.dataManager.util;

import java.util.Base64;

/**
 * @Author: dkx
 * @Date: 12:03 2021/1/14
 * @Desc:
 */
public class EnDecoderUtil {
    /**
     * base64加密
     * @param content 待加密内容
     * @return byte[]
     */
    public static byte[] base64Encrypt(final String content) {
        return Base64.getEncoder().encode(content.getBytes());
    }

    /**
     * base64解密
     * @param encoderContent 已加密内容
     * @return byte[]
     */
    public static byte[] base64Decrypt(final byte[] encoderContent) {
        return Base64.getDecoder().decode(encoderContent);
    }
}
