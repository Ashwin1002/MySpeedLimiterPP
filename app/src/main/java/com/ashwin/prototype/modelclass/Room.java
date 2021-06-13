package com.ashwin.prototype.modelclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Room {
    String room_name_pucblic;

    public Room() {

    }

    public Room(String room_name_pucblic) {
        this.room_name_pucblic = room_name_pucblic;
    }

    public String getRoom_name_pucblic() {
        return room_name_pucblic;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("room_name_pucblic", room_name_pucblic);

        return result;
    }
}