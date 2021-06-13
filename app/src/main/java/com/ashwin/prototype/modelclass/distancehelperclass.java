package com.ashwin.prototype.modelclass;

public class distancehelperclass {
    String userid,distance;

    public distancehelperclass() {
    }


    public distancehelperclass(String userid, String distance) {
        this.userid = userid;
        this.distance = distance;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
