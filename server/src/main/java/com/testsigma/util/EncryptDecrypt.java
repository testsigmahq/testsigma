package com.testsigma.util;

import lombok.extern.log4j.Log4j2;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Log4j2
public class EncryptDecrypt {
    private static final String ENCODE_FORMAT_UTF8 = "UTF-8";
    private static final String PUBLIC_KEY_SPEC = "lookfr";

    private Cipher dcipher, ecipher;

    public EncryptDecrypt() {
        // 8-bytes Salt
        byte[] salt = {(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03};

        // Iteration count
        int iterationCount = 19;

        try {
            // Generate a temporary key. In practice, you would save this key
            // Encrypting with DES Using a Pass Phrase
            KeySpec keySpec = new PBEKeySpec(PUBLIC_KEY_SPEC.toCharArray(), salt,
                    iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
                    .generateSecret(keySpec);

            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameters to the cipthers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
                    iterationCount);

            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        } catch (InvalidAlgorithmParameterException e) {
            log.error("EXCEPTION: InvalidAlgorithmParameterException");
        } catch (InvalidKeySpecException e) {
            log.error("EXCEPTION: InvalidKeySpecException");
        } catch (NoSuchPaddingException e) {
            log.error("EXCEPTION: NoSuchPaddingException");
        } catch (NoSuchAlgorithmException e) {
            log.error("EXCEPTION: NoSuchAlgorithmException");
        } catch (InvalidKeyException e) {
            log.error("EXCEPTION: InvalidKeyException");
        }
    }


    // Encrpt string
    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return Base64.getEncoder().encodeToString(enc).replaceAll("\n", "");

        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (Exception e) {
        }
        return null;
    }

    // Decrpt string
    // To decrypt the encryted password
    public String decrypt(String str) {
        try {
            // Decode base64 to get bytes
            byte[] dec = str == null ? "".getBytes() : Base64.getDecoder().decode(str);
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);
            // Decode using utf-8
            return new String(utf8, StandardCharsets.UTF_8);
        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        }
        return str;
    }

    // Encrpt string
    public String encrypt(byte[] utf8) {
        try {
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return Base64.getEncoder().encodeToString(enc);

        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        }
        return null;
    }

    // Decrpt string
    // To decrypt the encryted password
    public String decrypt(byte[] dec) {
        try {
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);
            // Decode using utf-8
            return new String(utf8, StandardCharsets.UTF_8);
        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        }
        return null;
    }
}

