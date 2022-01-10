package bgu.spl.net.api;
//package bgu.spl.net.api.bidi;
import bgu.spl.net.api.*;
import bgu.spl.net.srv.ConnectionHandler;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);
    public void connect(int connetcionId, ConnectionHandler connectionHandler);
}
