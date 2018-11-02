package com.adamyt.essay.struct;

public class EssayInfo implements Cloneable {
    public String title, url, type, cipherKey;
    public Long createTime, lastModifyTime, uid;    // essay's createTime should be different
    public boolean isPrivate;

    public void setCreateTime(){
        createTime = System.currentTimeMillis();
    }
    public void setUrl(){
        if(createTime==null) setCreateTime();
        String typeDir = isPrivate? "private/text/" : "public/text/";
        url = typeDir + createTime.toString() + ".md";
    }

    @Override
    public Object clone(){
        EssayInfo essay = null;
        try{
            essay = (EssayInfo) super.clone();
        }
        catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return essay;
    }
}
