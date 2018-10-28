package com.adamyt.essay.struct;

public class EssayInfo {
    public String username, title, url, type, cipherKey;
    public Long createTime, lastModifyTime;
    public boolean isPrivate;

    public EssayInfo(String url, String username, String title, String type, String cipherKey){
        this.url = url;
        this.username = username;
        this.title = title;
        this.type = type;
        this.isPrivate = cipherKey!=null;
        this.cipherKey = cipherKey;
        this.createTime = System.currentTimeMillis();
    }
}
