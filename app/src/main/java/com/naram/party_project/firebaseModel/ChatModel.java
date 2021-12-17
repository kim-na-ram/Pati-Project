package com.naram.party_project.firebaseModel;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저
    public Map<String,Message> message = new HashMap<>(); //채팅 메시지

    public static class Message {
        public String uid;
        public String name;
        public String picture;
        public String message;
        public Object timestamp;
    }
}