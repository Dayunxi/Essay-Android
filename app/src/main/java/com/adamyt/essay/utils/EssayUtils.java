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
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EssayUtils {
    public final static int REQUEST_WRITE_SOTRAGE = 0;
    public final static String EssayRootDir = Environment.getExternalStorageDirectory().toString()+"/com.adamyt.essay/";
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

    public static boolean addUser(Context context, String username, String password){
        if(needRequestWrite(context)) return false;
        return addItemToUserJson(username, password);
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
    public static boolean saveCiphertext(Context context, String username, String content, String title){
        if(!hasLoggedIn) return false;
        if(needRequestWrite(context)) return false;
        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
        String homePath = RelativeUserDir + username + "/private/text/";
        String unixTime = String.valueOf(System.currentTimeMillis());
        String filePath = homePath + unixTime + ".md";

        byte[] plainText = content.getBytes();
        // encryption
        byte[] cipherText = null;

        writeBytesTo(filePath, cipherText);
        addItemToEssayJson(filePath, username, title, "text", null);
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
    private static boolean addItemToUserJson(String username, String password){
        UserInfo user = new UserInfo(username, password);
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
    private static boolean addItemToEssayJson(String url, String username, String title, String type, String cipherKey){
        if(!hasLoggedIn) return false;
        String FilePath = CurrentUserHome + UserEssaysJsonName;
        EssayInfo essay = new EssayInfo(url, username, title, type, cipherKey);
        try {
            Gson gson = new Gson();
            String jsonString = null;
            byte[] byteStream = readBytesFrom(FilePath);

            if(byteStream!=null) jsonString = new String(byteStream);
            EssayInfo[] essays = gson.fromJson(jsonString, EssayInfo[].class);
            int length = essays==null? 1 : essays.length+1;
            EssayInfo[] newEssays = new EssayInfo[length];

            // Latest item in the top of json file
            newEssays[0] = essay;
            if(essays!=null) System.arraycopy(essays, 0, newEssays, 1, length-1);

            String newJson = gson.toJson(newEssays);
            return writeBytesTo(FilePath, newJson.getBytes());
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


}
