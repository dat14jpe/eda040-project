package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.omg.CORBA_2_3.portable.OutputStream;

public class Connection {
    private boolean connected;
    private String address;
    private int port;
    private Monitor monitor;
    private Socket socket;
    
    private Thread in;
    private Thread out;

    public Connection(String address, int port, Monitor monitor) {
        this.connected = false;
        this.address = address;
        this.port = port;
        this.monitor = monitor;
        
        try {
            this.socket = new Socket(address, port);
        } catch (Exception e) {
            throw new Error(e);
        }
        
    }
    public Socket getSocket() {
        return socket;
    }
}
