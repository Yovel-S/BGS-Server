package bgu.spl.net.impl.Bidi.messages;


import bgu.spl.net.api.Message;

public class NOTIFICATIONmsg extends Message {
    char notificationType;
    String postingUser;
    String Content;

    public NOTIFICATIONmsg(char notificationType, String postingUser, String content) {
        super((short) 9);
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        Content = content;
    }
    public NOTIFICATIONmsg(NOTIFICATIONmsg msg) {
        super((short) 9);
        notificationType = msg.getNotificationType();
        postingUser = msg.postingUser;
        Content = msg.getContent();
    }

    public char getNotificationType() {
        return notificationType;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return Content;
    }
}