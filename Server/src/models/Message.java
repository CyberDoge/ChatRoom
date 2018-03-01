package models;

import helper.Flag;

import java.util.Date;

public class Message {
    //TODO make private, getter & setter    etc bullshit
    public Date date;
    public String userName;
    public Integer userID;
    public Object mes;
    public Flag flag;
    public Message(){}
    public Message(Flag flag, Date date, Object mes) {
        this.date = date;
        this.mes = mes;
        this.flag = flag;
    }
    public Message(Flag flag, Date date, String userName, Integer userID) {
        this.date = date;
        this.flag = flag;
        this.userName = userName;
        this.userID = userID;
    }
}
