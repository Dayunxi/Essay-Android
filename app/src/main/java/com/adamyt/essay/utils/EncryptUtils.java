package com.adamyt.essay.utils;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
    private static final String AES_CIPHER_MODE = "AES/CBC/PKCS5Padding";
    private static final String CBC_IV = "_Essay.Adam.CBC_";

//    public static void main(String[] arg){
//        String content = "hhhhhhhh123";
//        String password = "1234";
//
//        byte[] cipherText = encryptAES(content, password);
//        String hex = byteToHex(cipherText);
//        System.out.printf("CipherText: %s\n", hex);
//
//        String plainText = decryptAES(cipherText, password);
//        System.out.printf("PlainText: %s\n", plainText);
//    }

    public static byte[] encryptAES(String content, String password){
        try{
            // generate a 64bits key from password
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes("utf-8"));
            keygen.init(128, secureRandom);
            byte[] key = keygen.generateKey().getEncoded();

            // initialize
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(AES_CIPHER_MODE);
            IvParameterSpec iv = new IvParameterSpec(CBC_IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            return cipher.doFinal(content.getBytes("utf-8"));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptAES(byte[] content, String password){
        try{
            // generate a 64bits key from password
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes("utf-8"));
            keygen.init(128, secureRandom);
            byte[] key = keygen.generateKey().getEncoded();

            // initialize
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(AES_CIPHER_MODE);
            IvParameterSpec iv = new IvParameterSpec(CBC_IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] plaintext = cipher.doFinal(content);

            return new String(plaintext, "utf-8");
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String base64Encode(byte[] cipherText){
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }
    private static byte[] base64Decode(String plainText){
        return Base64.decode(plainText, Base64.DEFAULT);
    }
}
