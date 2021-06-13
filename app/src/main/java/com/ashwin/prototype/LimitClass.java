package com.ashwin.prototype;

public class LimitClass {

    int speedlimit;
    String userid;
    public LimitClass() {
    }

    public LimitClass(String userid) {
        this.userid = userid;
    }

    public LimitClass(int speedlimit) {
        this.speedlimit = speedlimit;
    }

    public int getSpeedlimit() {
        return speedlimit;
    }

    public void setSpeedlimit(int speedlimit) {
        this.speedlimit = speedlimit;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
