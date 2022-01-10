package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;

public class LOGINmsg extends Message {
    private String username;
    private String password;
    private String captcha;

    public LOGINmsg(String _username, String _password , String _captcha){
        super((short)2);
        username = _username;
        password = _password;
        captcha = _captcha;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getCaptcha(){
        return captcha;
    }
}
