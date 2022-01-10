package bgu.spl.net.impl.Bidi.messages;

import bgu.spl.net.api.Message;

public class ERRORmsg extends Message {
    private short errorMsgOptNum;
    private String errorText;

    public ERRORmsg(short i){
        super((short)11);
        errorMsgOptNum = i;
        errorText = "";
    }

    public ERRORmsg(short i, String text){
        super((short)11);
        errorMsgOptNum = i;
        errorText = text;
    }

    public short getErrorMsgOptNum(){
        return errorMsgOptNum;
    }

    public String getErrorText(){
        return errorText;
    }
}
