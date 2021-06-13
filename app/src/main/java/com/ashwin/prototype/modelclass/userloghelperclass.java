package com.ashwin.prototype.modelclass;

public class userloghelperclass {
    String Gender, ImageUrl, UserName, UserPass;

    public userloghelperclass() {
    }

    public userloghelperclass(String gender, String imageUrl, String userName, String userPass) {
        Gender = gender;
        ImageUrl = imageUrl;
        UserName = userName;
        UserPass = userPass;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPass() {
        return UserPass;
    }

    public void setUserPass(String userPass) {
        UserPass = userPass;
    }
}
