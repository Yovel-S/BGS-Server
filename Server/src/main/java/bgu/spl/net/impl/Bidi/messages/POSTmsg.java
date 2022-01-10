package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.User;

import java.util.LinkedList;

public class POSTmsg extends Message {
    private String content;

    public POSTmsg(String _content){
        super((short) 5);
        content = _content;
    }

    public String getContent(){
        return content;
    }


}
