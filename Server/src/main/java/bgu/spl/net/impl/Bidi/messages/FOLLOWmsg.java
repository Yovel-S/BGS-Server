package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.User;

import java.util.LinkedList;

public class FOLLOWmsg extends Message {
    private byte followType;
    private String userForFollow;

    public FOLLOWmsg(byte _followType, String _username){
        super((short)4);
        followType = _followType;
        userForFollow = _username;
    }

    public String getUserForFollow(){
        return userForFollow;
    }

    public byte getFollowTypeByte(){
        return followType;
    }

    public String getFollowType(){
        byte i = 0;
        if(followType == i)
            return "FOLLOW";
        else{
            return "UNFOLLOW";
        }
    }


}
