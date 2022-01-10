package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.User;

import java.util.LinkedList;

public class BLOCKmsg extends Message {
    private String username;

    public BLOCKmsg(String _username){
        super((short) 12);
        username = _username;
    }

    public String getUsername(){
        return username;
    }


}
