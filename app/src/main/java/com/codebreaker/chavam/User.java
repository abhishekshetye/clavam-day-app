package com.codebreaker.chavam;

/**
 * Created by abhishek on 3/15/17.
 */

public class User {

    public String me;
    public String mobile;
    public String headquarter;
    public String device = "android";

    public User(String name, String mobile, String headquarter){
        this.me = name;
        this.mobile = mobile;
        this.headquarter = headquarter;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }
}
