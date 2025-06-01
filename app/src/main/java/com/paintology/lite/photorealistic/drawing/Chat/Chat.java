package com.paintology.lite.photorealistic.drawing.Chat;

public class Chat {


    public String sender = "";
    public String receiver = "";
    public String message = "";
    public String date = "";
    public String time = "";
    public String isMsgseen = "";
    public int msg_type = 0;

    public Chat(String sender, String receiver, String message, String date, String isMsgseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
        this.isMsgseen = isMsgseen;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsMsgseen() {
        return isMsgseen;
    }

    public void setIsMsgseen(String isMsgseen) {
        this.isMsgseen = isMsgseen;
    }
}
