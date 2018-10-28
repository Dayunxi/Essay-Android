package com.adamyt.essay.struct;

public class UserInfo {
    public String username, password, home;
    Config config;

    public UserInfo(String username, String password){
        this.username = username;
        this.password = password;
    }

    private class Config{

    }
}
