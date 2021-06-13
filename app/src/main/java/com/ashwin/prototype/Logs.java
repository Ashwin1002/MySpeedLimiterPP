package com.ashwin.prototype;

public class Logs {

    private  int imageProfile;
    private String TripName, TripDes;

    public Logs(int imageProfile, String tripName, String tripDes) {
        this.imageProfile = imageProfile;
        TripName = tripName;
        TripDes = tripDes;
    }

    public int getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(int imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getTripName() {
        return TripName;
    }

    public void setTripName(String tripName) {
        TripName = tripName;
    }

    public String getTripDes() {
        return TripDes;
    }

    public void setTripDes(String tripDes) {
        TripDes = tripDes;
    }
}
