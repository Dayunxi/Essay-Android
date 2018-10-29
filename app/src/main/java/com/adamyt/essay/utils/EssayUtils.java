package com.adamyt.essay.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.adamyt.essay.essay.R;
import com.adamyt.essay.struct.EssayInfo;
import com.adamyt.essay.struct.UserInfo;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EssayUtils {
    public final static int REQUEST_WRITE_SOTRAGE = 0;
    public final static String EssayRootDir = Environment.getExternalStorageDirectory().toString()+"/com.adamyt.essay/";
    public final static String AbsoluteUserDir = EssayRootDir+"user/";

    public final static String RelativeUserDir = "/user/";
    public final static String RelativeUserJsonPath = "/user.json";

    public final static String UserEssaysJsonName = "essays.json";
    
    public static String CurrentUsername;
    public static String CurrentUserHome;
    public static String CurrentUserPassword;
    public static boolean hasLoggedIn = false;
    public static boolean isAuthorized = false;

    public static ArrayList<EssayBean> getAllPublicEssay(Context context){
        ArrayList<EssayBean> essayList = new ArrayList<>();
        int iconLockUri;
        for(int i=0; i<20; i++){
            EssayBean essayBean = new EssayBean();
            essayBean.title = "俺寻思"+i;
            essayBean.date = new Date();
            if((i&1)==0) iconLockUri = R.drawable.ic_lock_open_black;
            else iconLockUri = R.drawable.ic_lock_black;
            essayBean.icon = context.getResources().getDrawable(iconLockUri);
            essayBean.essayUrl = "/user/adam/34sa.lock";
            essayList.add(essayBean);
        }
        return essayList;
    }

    public static String getEssayContent(String essayUrl){

        return null;
    }

    public static UserInfo[] getUserList(Context context){
        if(needRequestWrite(context)) return null;
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(RelativeUserJsonPath);

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


    public static boolean needRequestWrite(Context context) {
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
        File file = new File(EssayRootDir+filePath);
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
            File file = new File(EssayRootDir+filePath);
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
    public static boolean saveEssay(Context context, String content, EssayInfo essayInfo, boolean isNew){
        if(!hasLoggedIn){
            return false;
        }
        if(needRequestWrite(context)) return false;
        if(isNew) return saveNewEssay(essayInfo, content);
        else return saveModifiedEssay(essayInfo);
    }
    private static boolean saveModifiedEssay(EssayInfo essayInfo){
        return false;
    }
    private static boolean saveNewEssay(EssayInfo essayInfo, String content){

        String jsonPath = CurrentUserHome + UserEssaysJsonName;
        essayInfo.createTime = System.currentTimeMillis();
        if(essayInfo.isPrivate){
            String plainKey = getRandomKey();
        }
        else essayInfo.cipherKey = null;

//        if(essayInfo.title==null) essayInfo.title = getString();
        String typeDir = essayInfo.isPrivate? "/private/text/" : "/public/text/";
        String homePath = RelativeUserDir + essayInfo.username + typeDir;       // TODO: uid
        String unixTime = String.valueOf(System.currentTimeMillis());
        String filePath = homePath + unixTime + ".md";

        essayInfo.url = filePath;
        writeBytesTo(filePath, content.getBytes());

        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(jsonPath);

            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            int length = essays==null? 1 : essays.length+1;
            EssayInfo[] newEssays = new EssayInfo[length];

            // Latest item in the top of json file
            newEssays[0] = essayInfo;
            if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);

            String newJson = gson.toJson(newEssays);
            if(writeBytesTo(jsonPath, newJson.getBytes())){
                return true;
            }
            else{
                //del file
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

    public static boolean savePlaintext(Context context, String username, String content, String title){
        if(!hasLoggedIn) return false;
        if(needRequestWrite(context)) return false;
        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
        String homePath = RelativeUserDir + username + "/public/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String filePath = homePath + unixTime + ".md";

        return writeBytesTo(filePath, content.getBytes());
    }
    // TODO: AES & JSON & MD5
    public static boolean saveCiphertext(Context context, String content, String title){
        if(!hasLoggedIn) return false;
        if(needRequestWrite(context)) return false;
        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
        String homePath = CurrentUserHome + "/private/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String filePath = homePath + unixTime + ".md";

        byte[] plainText = content.getBytes();
        // encryption
        byte[] cipherText = null;

        writeBytesTo(filePath, cipherText);
        addItemToEssayJson(filePath, title, "text", null);
        return false;
    }

    // TODO: Call it in Activity.onCreate()
    public static String getUserHome(String username){
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(RelativeUserJsonPath);

            if(byteStream!=null) jsonString = new String(byteStream);
            UserInfo[] users = gson.fromJson(jsonString, UserInfo[].class);

            if(users!=null){
                for(UserInfo user : users){
                    if(user.username.equals(username)) return user.home;
                }
            }
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
        }
        return null;
    }

    //TODO: create a new user
    private static boolean addItemToUserJson(UserInfo user){
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(RelativeUserJsonPath);

            if(byteStream!=null) jsonString = new String(byteStream);
            UserInfo[] users = gson.fromJson(jsonString, UserInfo[].class);
            int length = users==null? 1 : users.length+1;
            UserInfo[] newUsers = new UserInfo[length];

            // Latest item in the top of json file
            newUsers[0] = user;
            if(users!=null) System.arraycopy(users, 0, newUsers, 1, length-1);

            String newJson = gson.toJson(newUsers);
            return writeBytesTo(RelativeUserJsonPath, newJson.getBytes());
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }
    // TODO:
    private static boolean addItemToEssayJson(String url, String title, String type, String cipherKey){
        if(!hasLoggedIn) return false;
        String jsonPath = CurrentUserHome + UserEssaysJsonName;
        EssayInfo essay = new EssayInfo(url, CurrentUsername, title, type, cipherKey);
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(jsonPath);

            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            int length = essays==null? 1 : essays.length+1;
            EssayInfo[] newEssays = new EssayInfo[length];

            // Latest item in the top of json file
            newEssays[0] = essay;
            if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);

            String newJson = gson.toJson(newEssays);
            return writeBytesTo(jsonPath, newJson.getBytes());
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<EssayBean> getAllEssay(Context context){
        if(!hasLoggedIn || !isAuthorized) return null;
        return null;
    }


    public static String getMD5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(s.getBytes());
            return toHex(bytes);
        }
        catch (Exception e) {
//            throw new RuntimeException(e);
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
