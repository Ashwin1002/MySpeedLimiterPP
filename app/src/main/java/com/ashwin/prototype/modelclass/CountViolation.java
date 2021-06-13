package com.ashwin.prototype.modelclass;

public class CountViolation {
    String name, parentID, userId, violation_count;

    public CountViolation() {
    }

    public CountViolation(String name, String parentID, String userId, String violation_count) {
        this.name = name;
        this.parentID = parentID;
        this.userId = userId;
        this.violation_count = violation_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getViolation_count() {
        return violation_count;
    }

    public void setViolation_count(String violation_count) {
        this.violation_count = violation_count;
    }
}
