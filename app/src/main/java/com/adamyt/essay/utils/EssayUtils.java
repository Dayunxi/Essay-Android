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
        File file = new File(filePath);
        try {
            if(!file.createNewFile()) return false;
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
            file.delete();
            e.printStackTrace();
            return false;
        }
        return true;
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

        return writeBytesTo(filePath, content.getBytes());
    }

    public static void getAllEssay(){

        String jsonRaw = "[\n" +
                "{\"username\": \"Adam\",\n" +
                "\"title\": \"Untitled\",\n" +
                "\"createTime\": 0,\n" +
                "\"url\": \"/user/Adam/public/text/0.md\",\n" +
                "\"isPrivate\": false,\n" +
                "\"lastModifyTime\": 0,\n" +
                "\"essayType\": \"text\"\n" +
                "},\n" +
                "{\"username\": \"Adam\",\n" +
                "\"title\": \"Untitled\",\n" +
                "\"createTime\": 2,\n" +
                "\"url\": \"/user/Adam/private/text/2.md\",\n" +
                "\"isPrivate\": true,\n" +
                "\"lastModifyTime\": 3,\n" +
                "\"essayType\": \"text\",\n" +
                "\"cipherKey\": \"sfas2354fdg76576gfr6767fdyt4654gh\"\n" +
                "}\n" +
                "]";
        String jsonUser = "[{\"username\": \"Adam\",\n" +
                "\"password\": \"45fdg2345dfgs234\",\n" +
                "\"config\":{\n" +
                "\"ttt\": \"23333\"\n" +
                "}\n" +
                "},\n" +
                "{\"username\": \"Dayunxi\",\n" +
                "\"password\": \"45f324sdsdfgs211\",\n" +
                "\"config\":{}\n" +
                "}]";
        Gson gson = new Gson();
        try {
//            EssayInfo[] infos = gson.fromJson(jsonRaw, EssayInfo[].class);
            UserInfo[] users = gson.fromJson(jsonUser, UserInfo[].class);

            for(UserInfo item : users){
                System.out.println(item.username);
                System.out.println(item.password);
                System.out.println(item.config.toString());
            }
        }
        catch (JsonSyntaxException e){
            e.printStackTrace();
        }


    }

    // /user/***/essayList.json
    private class EssayInfo{
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
