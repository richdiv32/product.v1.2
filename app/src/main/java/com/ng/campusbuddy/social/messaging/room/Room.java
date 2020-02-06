package com.ng.campusbuddy.social.messaging.room;

public class Room {

    String id_chatroom;
    String image_chatroom;
    String title_chatroom;
    String room_users;

    public Room(String id_chatroom, String image_chatroom, String title_chatroom, String room_users) {
        this.id_chatroom = id_chatroom;
        this.image_chatroom = image_chatroom;
        this.title_chatroom = title_chatroom;
        this.room_users = room_users;
    }

    public Room() {
    }

    public String getId_chatroom() {
        return id_chatroom;
    }

    public void setId_chatroom(String id_chatroom) {
        this.id_chatroom = id_chatroom;
    }

    public String getImage_chatroom() {
        return image_chatroom;
    }

    public void setImage_chatroom(String image_chatroom) {
        this.image_chatroom = image_chatroom;
    }

    public String getTitle_chatroom() {
        return title_chatroom;
    }

    public void setTitle_chatroom(String title_chatroom) {
        this.title_chatroom = title_chatroom;
    }

    public String getRoom_users() {
        return room_users;
    }

    public void setRoom_users(String room_users) {
        this.room_users = room_users;
    }
}
