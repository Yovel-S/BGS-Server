package bgu.spl.net.srv;
import bgu.spl.net.api.Connections;
import bgu.spl.net.srv.ConnectionHandler;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImp<T> implements Connections<T>{
    private ConcurrentHashMap<Integer, ConnectionHandler> IDtoConnHandler = new ConcurrentHashMap<>();

    public boolean send(int connID, T msg) {
        boolean sent = false;
        if(IDtoConnHandler.containsKey(connID)) {
            ConnectionHandler myConnection = IDtoConnHandler.get(connID);
            myConnection.send(msg);
            sent = true;
        }
        return sent;
    }

    public void broadcast(T msg) {
        for(ConnectionHandler ConnHandler: IDtoConnHandler.values()){
            ConnHandler.send(msg);
        }
    }

    public void disconnect(int connID) {
        try {
            IDtoConnHandler.get(connID).close();
            IDtoConnHandler.remove(connID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void connect(int connetcionId, ConnectionHandler connectionHandler) {
        IDtoConnHandler.put(connetcionId,connectionHandler);
    }
}
