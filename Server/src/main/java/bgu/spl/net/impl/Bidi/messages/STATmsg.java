
package bgu.spl.net.impl.Bidi.messages;
import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.User;

import java.util.LinkedList;

public class STATmsg extends Message {
    private String[] stringsOfUsernames;
    public STATmsg(String[] _stringsOfUsernames){
        super((short)8);
        stringsOfUsernames = _stringsOfUsernames;
    }

    public String[] getStringsOfUsernames() {
        return stringsOfUsernames;
    }
}
