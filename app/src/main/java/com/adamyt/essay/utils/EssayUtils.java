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

import java.io.File;
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

//    public static void savePlaintext(Context context, String username, String content, String title){
//        if(needRequestWrite(context)) return;
//        if(title == null) title = context.getResources().getString(R.string.essay_untitled);
//        String homePath = essayUserDir + username + "/";
//        String unixTime = String.valueOf(System.currentTimeMillis());
//        String filePath = homePath + unixTime + ".md";
//        File file = new File(filePath);
//        if(!file.exists()){
//
//        }
//    }
}
