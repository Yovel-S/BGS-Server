
package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.User;

import java.util.LinkedList;

public class PMmsg extends Message {
    private String username;
    private String content;
    private String date;

    public PMmsg(String _username, String _content, String _date){
        super((short) 6);
        username = _username;
        content = _content;
        date = _date;
    }

    public String getUsername(){
        return username;
    }
    public String getDate(){
        return date;
    }
    public String getContent(){
        return content;
    }


}
