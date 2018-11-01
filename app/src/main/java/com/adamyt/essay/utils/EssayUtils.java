package com.adamyt.essay.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.adamyt.essay.struct.EssayInfo;
import com.adamyt.essay.struct.UserInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EssayUtils {
    private final static String EssayRootDir = Environment.getExternalStorageDirectory().toString()+"/com.adamyt.essay/";
    private final static String AbsoluteUserDir = EssayRootDir+"user/";
    private final static String AbsoluteUserJsonPath = EssayRootDir+"user.json";
    private final static String UserEssaysJsonName = "essays.json";

    public final static int REQUEST_WRITE_SOTRAGE = 0;
    public static UserInfo CurrentUser;
    public static boolean hasLoggedIn = false;
    public static boolean isAuthorized = false;

    private static String plainPassword = null;

    public static void setPassword(String password){
        plainPassword = password;
    }

    public static ArrayList<EssayInfo> getAllPublicEssay(Context context){
        if(CurrentUser == null) return null;
        if(needRequestWrite(context)) return null;
        ArrayList<EssayInfo> essayList = new ArrayList<>();

        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";
        String jsonPath = currentUserHome + UserEssaysJsonName;

        try {
            // get json of user's essays
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(jsonPath);
            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            if(essays != null) Collections.addAll(essayList, essays);
            return essayList;
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<EssayInfo> getAllEssay(Context context){
        if(CurrentUser == null) return null;
        if(needRequestWrite(context)) return null;
        ArrayList<EssayInfo> essayList = new ArrayList<>();

        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";
        String jsonPath = currentUserHome + UserEssaysJsonName;

        try {
            // get json of user's essays
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(jsonPath);
            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            if(essays != null) Collections.addAll(essayList, essays);
            return essayList;
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEssayContent(Context context, EssayInfo essayInfo){
        if(needRequestWrite(context)) return null;
        String filePath = AbsoluteUserDir + essayInfo.uid.toString() + "/" + essayInfo.url;
        String content;
        byte[] byteStream = readBytesFrom(filePath);
        if(byteStream == null) return null;
        if(essayInfo.isPrivate){
            if(!isAuthorized || plainPassword==null) return null;

            byte[] cipherByte = EncryptUtils.base64Decode(essayInfo.cipherKey);
            String essayKey = EncryptUtils.decryptAES(cipherByte, plainPassword);
            content = EncryptUtils.decryptAES(byteStream, essayKey);
        }
        else content = new String(byteStream);

        return content;
    }

    public static UserInfo getUserInfo(Context context, Long uid){
        if(needRequestWrite(context)) return null;
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(AbsoluteUserJsonPath);
            if(byteStream!=null) jsonString = new String(byteStream);
            UserInfo[] users =  gson.fromJson(jsonString, UserInfo[].class);

            if(users==null) return null;
            for(UserInfo user : users){ // Be sure Essay.CurrentUser is independent
                if(user.uid.equals(uid)) return (UserInfo) user.clone();
            }
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
        }
        return null;
    }

    public static UserInfo[] getUserList(Context context){
        if(needRequestWrite(context)) return null;
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(AbsoluteUserJsonPath);

            if(byteStream!=null) jsonString = new String(byteStream);

            return gson.fromJson(jsonString, UserInfo[].class);
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addUser(Context context, UserInfo user){
        if(needRequestWrite(context)) return false;
        return addItemToUserJson(user);
    }

    public static boolean saveEssay(Context context, String content, EssayInfo essayInfo, boolean isNew){
        if(!hasLoggedIn) return false;
        if(needRequestWrite(context)) return false;
        if(isNew) return saveNewEssay(essayInfo, content);
        return saveModifiedEssay(essayInfo, content);
    }


    private static boolean needRequestWrite(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (ActivityCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        // FIXME: abstract class Context can be cast or convert to derived class Activity?
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context, "We need it!", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions((Activity)context, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_SOTRAGE);
        return true;
    }

    private static boolean writeBytesTo(String filePath, byte[] byteStream){
        File file = new File(filePath);
        File dir = file.getParentFile();
        try {
            dir.mkdirs();
            if(!file.createNewFile()) System.out.println("This file has already existed, replace it.");
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }

        try {
            FileOutputStream ostream = new FileOutputStream(file);
            ostream.write(byteStream);
            ostream.close();
        }
        catch (IOException e){
//            file.delete();        // dangerous for user.json
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static byte[] readBytesFrom(String filePath){
        try {
            File file = new File(filePath);
            if(!file.exists() || file.length()==0) return null;
            byte[] byteStream = new byte[(int)file.length()];
            FileInputStream istream = new FileInputStream(file);
            istream.read(byteStream);
            istream.close();
            return byteStream;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static boolean saveEssayFile(EssayInfo essayInfo, String content){

        if(!isAuthorized){
            //TODO: show authorize dialog
            System.out.println("not authorize");
            return false;
        }
        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";
        try{
            byte[] essayBytes;
            // encrypt or not
            if(essayInfo.isPrivate){
                String essayKey = getRandomKey();
                byte[] cipherByte = EncryptUtils.encryptAES(essayKey, plainPassword);
                essayInfo.cipherKey = EncryptUtils.base64Encode(cipherByte);

                essayBytes = EncryptUtils.encryptAES(content, essayKey);
            }
            else{
                essayInfo.cipherKey = null;
                essayBytes = content.getBytes("utf-8");
            }

            // save essay's content
            return writeBytesTo(currentUserHome+essayInfo.url, essayBytes);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private static boolean saveModifiedEssay(EssayInfo essayInfo, String content){
        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";

        if(!saveEssayFile(essayInfo, content)) return false;

        try {
            // get json of user's essays
            Gson gson = new Gson();
            String jsonString = null;
            String jsonPath = currentUserHome + UserEssaysJsonName;
            byte[] byteStream = readBytesFrom(jsonPath);
            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);

            if(essays==null) return false;
            for(int i=0; i<essays.length; i++){
                if(essays[i].url.equals(essayInfo.url)){
                    essays[i] = essayInfo;
                }
            }

            // save json
            byteStream = gson.toJson(essays).getBytes("utf-8");
            if(writeBytesTo(jsonPath, byteStream)){
                System.out.println("success");
                return true;
            }
            else return false;
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return false;
        }
    }
    private static boolean saveNewEssay(EssayInfo essayInfo, String content){
        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";

        if(!saveEssayFile(essayInfo, content)) return false;

        try {
            // get json of user's essays
            Gson gson = new Gson();
            String jsonString = null;
            String jsonPath = currentUserHome + UserEssaysJsonName;
            byte[] byteStream = readBytesFrom(jsonPath);
            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            // append or modify
            int length = essays==null? 1 : essays.length+1;
            EssayInfo[] newEssays = new EssayInfo[length];
            // Latest item in the top of json file
            newEssays[0] = essayInfo;
            if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);

            // save json
            byteStream = gson.toJson(newEssays).getBytes("utf-8");
            if(writeBytesTo(jsonPath, byteStream)){
                System.out.println("success");
                return true;
            }
            else{
                //del file
                String filePath = currentUserHome + essayInfo.url;
                File file = new File(filePath);
                file.delete();
                return false;
            }
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return false;
        }
    }


    private static String getRandomKey(){
        final int KEY_LENGTH = 16;
        final char[] charSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        SecureRandom rand = new SecureRandom();
        for(int i=0; i<KEY_LENGTH; i++){
            key.append(charSet[rand.nextInt(62)]);
        }
        return key.toString();
    }


    //TODO: create a new user
    private static boolean addItemToUserJson(UserInfo user){
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(AbsoluteUserJsonPath);

            if(byteStream!=null) jsonString = new String(byteStream);
            UserInfo[] users = gson.fromJson(jsonString, UserInfo[].class);
            int length = users==null? 1 : users.length+1;
            UserInfo[] newUsers = new UserInfo[length];

            // Latest item in the top of json file
            newUsers[0] = user;
            if(users!=null) System.arraycopy(users, 0, newUsers, 1, length-1);

            byteStream = gson.toJson(newUsers).getBytes("utf-8");
            return writeBytesTo(AbsoluteUserJsonPath, byteStream);
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return false;
        }
    }


//    public static String getMD5(String s) {
//        try {
//            MessageDigest md5 = MessageDigest.getInstance("MD5");
//            byte[] bytes = md5.digest(s.getBytes("utf-8"));
//            return byteToHex(bytes);
//        }
//        catch (Exception e) {
//            return null;
//        }
//    }
//    private static String byteToHex(byte[] bytes) {
//        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
//        StringBuilder ret = new StringBuilder(bytes.length * 2);
//        for(byte item : bytes){
//            ret.append(HEX_DIGITS[(item>>4) & 0x0f]);
//            ret.append(HEX_DIGITS[item & 0x0f]);
//        }
//        return ret.toString();
//    }
//    private static byte[] hexToByte(String hex){
//        return null;
//    }

}
