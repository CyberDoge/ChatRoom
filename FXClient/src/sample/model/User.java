package sample.model;

import java.util.ArrayList;


public class User {
    private String name;
    private Integer userID;
    private ArrayList<Message> messages;

    {
        this.messages = new ArrayList<>();
    }

    public User() {
    }

    public User(String name, Integer userID) {
        this.name = name;
        this.userID = userID;
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
