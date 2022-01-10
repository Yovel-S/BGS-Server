package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.api.Message;
import bgu.spl.net.srv.ConnectionsImp;
import bgu.spl.net.impl.Bidi.BidiMessageEncoderDecoder;
import bgu.spl.net.impl.Bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bidi.Database;
import bgu.spl.net.srv.Server;
import java.util.function.Supplier;

public class ReactorMain {
    public static void main(String[] args) {
        Database db=new Database();
        Supplier<MessageEncoderDecoder<Message>> encdecSupplier = BidiMessageEncoderDecoder::new;
        Supplier<BidiMessagingProtocol<Message>> protocolSupplier= ()->new BidiMessagingProtocolImpl(db);

        Server.reactor(
                Integer.parseInt(args[0]), //port
                Integer.parseInt(args[1]),//numOfThreads
                protocolSupplier,
                encdecSupplier,
                new ConnectionsImp<Message>()
        ).serve();
    }
}
