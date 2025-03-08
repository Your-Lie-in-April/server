package com.appcenter.timepiece.global.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.EncodingException;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class AESEncoder {
    @Value("${util.encoder.invitation-link-encoder.alg}")
    private String alg;

    @Value("${util.encoder.invitation-link-encoder.key}")
    private String key;

    @Value("${util.encoder.invitation-link-encoder.iv}")
    private String iv;

    public String encryptAES256(String text) {
        Cipher cipher;
        byte[] encrypted;

        try {
            cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to init: encoder");
        }

        try {
            encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncodingException("Failed to encoding: generate Invitation Link");
        }
        return Base64.encodeBase64URLSafeString(encrypted);
    }

    public String decryptAES256(String cipherText) {
        Cipher cipher;
        byte[] decrypted;

        try {
            cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to init: decoder");
        }

        try {
            byte[] decodedBytes = Base64.decodeBase64URLSafe(cipherText);
            decrypted = cipher.doFinal(decodedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("Failed to decoding: invalid Invitation Link");
        }
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
