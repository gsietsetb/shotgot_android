package com.shotgot.shotgot.api;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by gsierra on 31/01/17.
 */

public class SocketIO {
    private String host = "shotgot.com";//"192.168.1.15";
    private String port = "3001";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://" + host + ":" + port);//https://shotgot.com:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getSocket() {
        return mSocket;
    }
}
