package com.adamyt.essay.utils;

import android.content.Context;

import com.adamyt.essay.essay.R;

import java.util.ArrayList;
import java.util.Date;

public class EssayUtils {
    public static ArrayList<EssayBean> getAllEssay(Context context){
        ArrayList<EssayBean> essayList = new ArrayList<>();
        for(int i=0; i<10; i++){
            EssayBean essayBean = new EssayBean();
            essayBean.title = "俺寻思";
            essayBean.date = new Date();
            essayBean.icon = context.getResources().getDrawable(R.drawable.ic_menu_lock_open);
            essayBean.essayUrl = "/user/adam/34sa.lock";
            essayList.add(essayBean);
        }
        return essayList;
    }

    public static String getEssayContent(String essayUrl){

        return null;
    }
}
