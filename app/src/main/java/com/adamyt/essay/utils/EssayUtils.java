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
import java.security.MessageDigest;
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
        if(!hasLoggedIn || !isAuthorized) return null;
        return null;
    }

    public static String getEssayContent(Context context, EssayInfo essayInfo){
        if(needRequestWrite(context)) return null;
        String filePath = AbsoluteUserDir + essayInfo.uid.toString() + "/" + essayInfo.url;
        String content = null;
        if(essayInfo.isPrivate){

        }
        else{
            byte[] byteStream = readBytesFrom(filePath);
            if(byteStream!=null) content = new String(byteStream);
        }

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

    public static boolean saveUserEssay(Context context, String content, EssayInfo essayInfo, boolean isNew){
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
        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";
        // encrypt or not
        byte[] essayBytes = null;
        if(essayInfo.isPrivate){
            String plainKey = getRandomKey();
            // encryption
//            essayInfo.cipherKey = plainKey;
        }
        else{
            essayInfo.cipherKey = null;
            essayBytes = content.getBytes();
        }

        // save essay's content
        return writeBytesTo(currentUserHome+essayInfo.url, essayBytes);
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
            String newJson = gson.toJson(essays);
            // save json
            if(writeBytesTo(jsonPath, newJson.getBytes())){
                System.out.println("success");
                return true;
            }
            else return false;
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }
    private static boolean saveNewEssay(EssayInfo essayInfo, String content){
        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";

        // create the url of essay
        String typeDir = essayInfo.isPrivate? "private/text/" : "public/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String relativeFilePath = typeDir + unixTime + ".md";
        String filePath = currentUserHome + relativeFilePath;

        // member title, type, isPrivate, uid, createTime has been assigned in EditActivity.
        essayInfo.url = relativeFilePath;
        essayInfo.createTime = System.currentTimeMillis();

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
            String newJson = gson.toJson(newEssays);

            // save json
            if(writeBytesTo(jsonPath, newJson.getBytes())){
                System.out.println("success");
                return true;
            }
            else{
                //del file
                File file = new File(filePath);
                file.delete();
                return false;
            }
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }


    private static boolean saveEssay(EssayInfo essayInfo, String content, boolean isNew){
        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";
        String jsonPath = currentUserHome + UserEssaysJsonName;
        essayInfo.createTime = System.currentTimeMillis();

        // create the url of essay
        String typeDir = essayInfo.isPrivate? "private/text/" : "public/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String relativeFilePath = typeDir + unixTime + ".md";
        String filePath = currentUserHome + relativeFilePath;
        essayInfo.url = relativeFilePath;

        // encrypt or not
        byte[] essayBytes = null;
        if(essayInfo.isPrivate){
            String plainKey = getRandomKey();
            // encryption
//            essayInfo.cipherKey = plainKey;
        }
        else{
            essayInfo.cipherKey = null;
            essayBytes = content.getBytes();
        }

        // save essay's content
        if(!writeBytesTo(filePath, essayBytes)) return false;

        try {
            // get json of user's essays
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(jsonPath);
            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            System.out.println("isNew: "+isNew);
            // append or modify
            String newJson;
            if(isNew){
                int length = essays==null? 1 : essays.length+1;
                EssayInfo[] newEssays = new EssayInfo[length];
                // Latest item in the top of json file
                newEssays[0] = essayInfo;
                if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);
                newJson = gson.toJson(newEssays);
            }
            else{
                System.out.println("!!!!!!!!!!!");
                System.out.printf("Title: %s Url: %s\n", essayInfo.title, essayInfo.url);
                if(essays==null) return false;
                for(int i=0; i<essays.length; i++){
                    if(essays[i].url.equals(essayInfo.url)){
                        System.out.println("???????????????");
                        essays[i] = essayInfo;
                    }
                }

                newJson = gson.toJson(essays);
                System.out.printf("Json: %s\n", newJson);
            }

            // save json
            if(writeBytesTo(jsonPath, newJson.getBytes())){
                System.out.println("success");
                return true;
            }
            else{
                //del file
                File file = new File(filePath);
                file.delete();
                return false;
            }
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }

    private static String getRandomKey(){
        final int KEY_LENGTH = 16;
        final char[] charSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        Random rand = new Random();
        for(int i=0; i<KEY_LENGTH; i++){
            key.append(charSet[rand.nextInt(62)]);
        }
        return key.toString();
    }

//    public static boolean savePlaintext(Context context, String username, String content, String title){
//        if(!hasLoggedIn) return false;
//        if(needRequestWrite(context)) return false;
//        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
//        String homePath = AbsoluteUserDir + username + "/public/text/";
//        String unixTime = String.valueOf(System.currentTimeMillis());
//        String filePath = homePath + unixTime + ".md";
//
//        return writeBytesTo(filePath, content.getBytes());
//    }
//    // TODO: AES & JSON & MD5
//    public static boolean saveCiphertext(Context context, String content, String title){
//        if(!hasLoggedIn) return false;
//        if(needRequestWrite(context)) return false;
//        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
//        String currentUserHome = AbsoluteUserDir + CurrentUser.uid.toString() + "/";
//        String homePath = currentUserHome + "/private/text/";
//        String unixTime = String.valueOf(System.currentTimeMillis());
//        String filePath = homePath + unixTime + ".md";
//
//        byte[] plainText = content.getBytes();
//        // encryption
//        byte[] cipherText = null;
//
//        writeBytesTo(filePath, cipherText);
////        addItemToEssayJson(filePath, title, "text", null);
//        return false;
//    }


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

            String newJson = gson.toJson(newUsers);
            return writeBytesTo(AbsoluteUserJsonPath, newJson.getBytes());
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }

    //    private static boolean addItemToEssayJson(String url, String title, String type, String cipherKey){
//        if(!hasLoggedIn) return false;
//        String jsonPath = CurrentUser.home + UserEssaysJsonName;
//        EssayInfo essay = new EssayInfo(url, CurrentUser.uid, title, type, cipherKey);
//        try {
//            Gson gson = new Gson();
//            String jsonString = null;
//            byte[] byteStream = readBytesFrom(jsonPath);
//
//            if(byteStream!=null) jsonString = new String(byteStream);
//            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
//            int length = essays==null? 1 : essays.length+1;
//            EssayInfo[] newEssays = new EssayInfo[length];
//
//            // Latest item in the top of json file
//            newEssays[0] = essay;
//            if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);
//
//            String newJson = gson.toJson(newEssays);
//            return writeBytesTo(jsonPath, newJson.getBytes());
//        }
//        catch (JsonSyntaxException e){
//            e.printStackTrace();
//            return false;
//        }
//    }




    public static String getMD5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(s.getBytes());
            return toHex(bytes);
        }
        catch (Exception e) {
            return null;
        }
    }
    private static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for(byte item : bytes){
            ret.append(HEX_DIGITS[(item>>4) & 0x0f]);
            ret.append(HEX_DIGITS[item & 0x0f]);
        }
        return ret.toString();
    }

}
