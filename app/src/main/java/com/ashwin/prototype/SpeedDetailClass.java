package com.ashwin.prototype;

public class SpeedDetailClass {
  String userId, tripT, tripD, dist, max, avg, loc;


    public SpeedDetailClass() {
    }

    public SpeedDetailClass(String userId, String tripT, String tripD, String dist, String max, String avg, String loc) {
        this.userId = userId;
        this.tripT = tripT;
        this.tripD = tripD;
        this.dist = dist;
        this.max = max;
        this.avg = avg;
        this.loc = loc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripT() {
        return tripT;
    }

    public void setTripT(String tripT) {
        this.tripT = tripT;
    }

    public String getTripD() {
        return tripD;
    }

    public void setTripD(String tripD) {
        this.tripD = tripD;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
