package client;

import java.net.Socket;

public class Connection {
    private boolean connected;
    private String address;
    private int port;
    //private Monitor monitor;
    private Socket socket;

    private long timer;

    public Connection(String address, int port, Monitor monitor) {
        this.connected = false;
        this.address = address;
        this.port = port;
        //this.monitor = monitor;
        this.timer = System.currentTimeMillis();

        final long delay = 0L;//3000L;
        final int maxTries = 1;
        int tries = 0;
        while (!connected && tries < maxTries) {
            long delta = System.currentTimeMillis() - timer;
            if (delta >= delay) {
                connect();
                ++tries;
            }
        }
    }

    private void connect() {
        this.timer = System.currentTimeMillis();
        try {
            this.socket = new Socket(address, port);
            this.connected = true;
        } catch (Exception e) {}
    }

    public Socket getSocket() {
        return socket;
    }
    
    public boolean isConnected() {
        return connected;
    }
}
