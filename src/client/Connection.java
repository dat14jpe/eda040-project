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

    private long timer;

    private Thread in;
    private Thread out;

    public Connection(String address, int port, Monitor monitor) {
        this.connected = false;
        this.address = address;
        this.port = port;
        this.monitor = monitor;
        this.timer = System.currentTimeMillis();

        while (!connected) {
            long delta = System.currentTimeMillis() - timer;
            if (delta > 3000L) {
                connect();
            }
        }
    }

    private void connect() {
        this.timer = System.currentTimeMillis();
        try {
            this.socket = new Socket(address, port);
            this.connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
