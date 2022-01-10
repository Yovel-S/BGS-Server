package bgu.spl.net.api;

public abstract class Message {
    private short Optcode;

    public Message(){}
    public Message(short op){
        Optcode = op;
    }

    public short getOpcode() {
        return Optcode;
    }


}
