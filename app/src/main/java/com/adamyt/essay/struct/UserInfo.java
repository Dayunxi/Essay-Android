package com.adamyt.essay.struct;

public class UserInfo implements Cloneable {
    public String username, password;
    public Long uid;
    Config config;

//    public UserInfo(String username, String password){
//        this.username = username;
//        this.password = password;
//    }

    public UserInfo(String username, String password){
        this.username = username;
        this.password = password;
        this.uid = System.currentTimeMillis();
//        this.home = userDir+"/"+uid.toString();
    }

    private class Config{

    }

    @Override
    public Object clone(){
        UserInfo user = null;
        try{
            user = (UserInfo) super.clone();
        }
        catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return user;
    }
}
