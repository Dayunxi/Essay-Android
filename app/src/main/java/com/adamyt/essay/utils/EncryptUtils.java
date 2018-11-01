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
    private static byte[] convertPassword(String password){
        final int PWD_SIZE = 16;
        try{
            if (password == null) password = "";
            StringBuilder ret = new StringBuilder(PWD_SIZE);
            ret.append(password);
            while (ret.length() < PWD_SIZE) ret.append("0");
            if (ret.length() > PWD_SIZE) ret.setLength(PWD_SIZE);

            return ret.toString().getBytes("utf-8");
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    static byte[] encryptAES(String content, String password){
        try{
            // generate a 64bits key from password
//            KeyGenerator keygen = KeyGenerator.getInstance("AES");
//            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//            secureRandom.setSeed(password.getBytes("utf-8"));
//            keygen.init(128, secureRandom);
//            byte[] key = keygen.generateKey().getEncoded();

            byte[] key = convertPassword(password);
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

    static String decryptAES(byte[] content, String password){
        try{
            // generate a 64bits key from password
//            KeyGenerator keygen = KeyGenerator.getInstance("AES");
//            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//            secureRandom.setSeed(password.getBytes("utf-8"));
//            keygen.init(128, secureRandom);
//            byte[] key = keygen.generateKey().getEncoded();

            byte[] key = convertPassword(password);
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

    static String base64Encode(byte[] cipherText){
        return Base64.encodeToString(cipherText, Base64.NO_PADDING|Base64.NO_WRAP);
    }
    static byte[] base64Decode(String plainText){
        return Base64.decode(plainText, Base64.NO_PADDING|Base64.NO_WRAP);
    }

    static String byteToHex(byte[] bytes) {
        if(bytes==null) return null;
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for(byte item : bytes){
            ret.append(HEX_DIGITS[(item>>4) & 0x0f]);
            ret.append(HEX_DIGITS[item & 0x0f]);
        }
        return ret.toString();
    }
    static byte[] hexToByte(String str) {
        if (str == null || str.length() < 2) {
            return new byte[0];
        }
        str = str.toLowerCase();
        int l = str.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = str.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }
}
