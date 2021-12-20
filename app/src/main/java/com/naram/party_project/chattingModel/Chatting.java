package com.naram.party_project.chattingModel;

import java.util.HashMap;
import java.util.Map;

public class Chatting {
    public Map<String,UserInfo> users = new HashMap<>(); //채팅방 유저
    public Map<String,Message> message = new HashMap<>(); //채팅 메시지

    public static class Message {
        public String uid;
        public String message;
        public Object timestamp;
    }

    public static class UserInfo {
        public String name;
        public String picture;
    }
}