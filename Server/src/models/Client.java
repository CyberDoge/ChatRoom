package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.DataBase;
import helper.Flag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonRootName("User")
public class Client implements Runnable {
    // static int cid = 0;
    @JsonProperty("userID")
    private Integer userId;
    @JsonProperty("name")
    private String userName;
    //@JsonProperty("messages")
    @JsonIgnore
    private List<Message> messageList;

    @JsonIgnore
    private String password;
    @JsonIgnore
    private Room room;
    @JsonIgnore
    private ConnectionFromClient connection;
    @JsonIgnore
    private ObjectMapper mapper;
    @JsonIgnore
    private boolean alive = true;

    public Client() {
    }

    public Client(ConnectionFromClient connection, Room room, Integer userId, String userName) {
        this.connection = connection;
        this.room = room;
        this.userId = userId;
        this.userName = userName;
        messageList = new ArrayList<>();
        mapper = new ObjectMapper();
    }

    public ConnectionFromClient getConnection() {
        return connection;
    }

    public void setConnection(ConnectionFromClient connection) {
        this.connection = connection;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        while (alive) {
            String line = connection.getLine();
            Message message;
            try {
                message = mapper.readValue(line, Message.class);
                message.userName = userName;
                message.userID = userId;

                messageList.add(message);
            } catch (IOException e) {
                e.printStackTrace();
                dispose();
                break;
            }
            if (message.flag == Flag.EXIT) dispose();
            else if (message != null) {
                DataBase.addMessage(message);
                room.sendMessageForAll(message);
            } else dispose();
        }
    }

    public void dispose() {
        alive = false;

        //checkDBInsert();
        connection.dispose();
        messageList = null;
        mapper = null;
        room.removeClient(this);
        Thread.currentThread().interrupt();
    }
    //don't if it need
    public boolean checkDBInsert() {
        System.out.println("checking  if all info in database");
        return true;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
