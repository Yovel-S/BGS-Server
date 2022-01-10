package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;

public class REGISTERmsg extends Message {
    private String username;
    private String password;
    private String birthday; // format DD-MM-YYYY

    public REGISTERmsg(String _username, String _password , String _birthday){
        super((short)1);
        username = _username;
        password = _password;
        birthday = _birthday;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getBirthday(){
        return birthday;
    }
}
