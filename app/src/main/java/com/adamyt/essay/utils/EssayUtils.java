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
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EssayUtils {
    public final static int REQUEST_WRITE_SOTRAGE = 0;
    public final static String essayRootDir = Environment.getExternalStorageDirectory().toString()+"/com.adamyt.essay/";
    public final static String essayConfigDir = essayRootDir+"config/";
    public final static String essayUserDir = essayRootDir+"user/";

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
        File file = new File(essayRootDir+filePath);
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
            File file = new File(essayRootDir+filePath);
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

    public static boolean savePlaintext(Context context, String username, String content, String title){
        if(needRequestWrite(context)) return false;
        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
        String homePath = essayUserDir + username + "/public/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String filePath = homePath + unixTime + ".md";

        return writeBytesTo(filePath, content.getBytes());
    }
    // TODO: AES & JSON & MD5
    public static boolean saveCiphertext(Context context, String username, String content, String title){
        if(needRequestWrite(context)) return false;
        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
        String homePath = essayUserDir + username + "/private/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String filePath = homePath + unixTime + ".md";

        // encryption
        writeBytesTo(filePath, content.getBytes());
        addItemToEssayJson(filePath, username, title, "text", null);
        return false;
    }


    public void addItemToUserJson(){

    }
    private static boolean addItemToEssayJson(String filePath, String username, String title, String type, String cipherKey){
        EssayInfo essay = new EssayInfo();
        essay.url = filePath;
        essay.username = username;
        essay.title = title;
        essay.essayType = type;
        essay.isPrivate = cipherKey!=null;
        essay.cipherKey = cipherKey;
        essay.createTime = System.currentTimeMillis();
        try {
            Gson gson = new Gson();
            String jsonUser = null;
            byte[] byteStream = readBytesFrom(filePath);

            if(byteStream!=null) jsonUser = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonUser, EssayInfo[].class);
            int length = essays==null? 1 : essays.length+1;
            EssayInfo[] newEssays = new EssayInfo[length];

            // Newest item in the top of json file
            newEssays[0] = essay;
            if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);

            String newJson = gson.toJson(newEssays);
            return writeBytesTo(filePath, newJson.getBytes());
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void getAllEssay(){


    }

    // /user/***/essayList.json
    private static class EssayInfo{
        String username, title, url, essayType, cipherKey;
        Long createTime, lastModifyTime;
        boolean isPrivate;
    }

    // /users.json
    private class Config{

    }
    private class UserInfo{
        String username, password;
        Config config;
    }
}
