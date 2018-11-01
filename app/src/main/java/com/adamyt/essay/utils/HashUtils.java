package com.adamyt.essay.utils;

import java.security.MessageDigest;

public class HashUtils {

    public static String getMD5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(s.getBytes("utf-8"));
            return byteToHex(bytes);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static String byteToHex(byte[] bytes) {
        if(bytes==null) return null;
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for(byte item : bytes){
            ret.append(HEX_DIGITS[(item>>4) & 0x0f]);
            ret.append(HEX_DIGITS[item & 0x0f]);
        }
        return ret.toString();
    }
}
