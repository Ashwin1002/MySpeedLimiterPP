package com.ashwin.prototype;

public class OverSpeedHelperClass {
    String violation;
    String userid, email;
    public OverSpeedHelperClass() {
    }

    public OverSpeedHelperClass(String violation, String userid, String email) {
        this.violation = violation;
        this.userid = userid;
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getviolation() {
        return violation;
    }

    public void setviolation(String violation) {
        this.violation = violation;
    }
}
