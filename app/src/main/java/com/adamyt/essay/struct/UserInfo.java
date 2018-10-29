package com.adamyt.essay.struct;

public class UserInfo {
    public String username, password, home;
    public Long uid;
    Config config;

    public UserInfo(String username, String password){
        this.username = username;
        this.password = password;
    }

    public UserInfo(String username, String password, String userDir){
        this.username = username;
        this.password = password;
        this.uid = System.currentTimeMillis();
        this.home = userDir+"/"+uid.toString();
    }

    private class Config{

    }
}
