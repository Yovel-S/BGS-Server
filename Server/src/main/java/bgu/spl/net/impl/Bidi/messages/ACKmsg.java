package bgu.spl.net.impl.Bidi.messages;


import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.User;

import java.util.List;

public class ACKmsg extends Message {
    private class UserList{

    }
    short msgOpcode;
    short numPosts;
    short numFollowing;
    short numOfUsers;
    String username;
    byte followByte;
    List<User> userList;
    User user;

    // REGISTER & LOGOUT
    public ACKmsg(short msgOpcode) {
        super((short) 10);
        this.msgOpcode = msgOpcode;
    }
    // STAT&LOGSTAT
    public ACKmsg(short msgOpcode, List<User> userList){
        super((short) 10);
        this.msgOpcode = msgOpcode;
        this.userList =  userList;
    }
    public ACKmsg(short msgOpcode, User _user){
        super((short) 10);
        this.msgOpcode = msgOpcode;
        user = _user;
    }
    // FOLLOW
    public ACKmsg(byte followByte, String username){
        super((short) 10);
        this.msgOpcode = 4; // follow opcode
        this.followByte = followByte;
        this.username = username;
    }

    public short getMsgOpcode() {
        return msgOpcode;
    }

    public short getNumPosts() {
        return numPosts;
    }

    public short getNumFollowing() {
        return numFollowing;
    }

    public List<User> getUserList() {
        return userList;
    }

    public String getUserName() {
        return username;
    }

    public User getUser(){
        return user;
    }
}

