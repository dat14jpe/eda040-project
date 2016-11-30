package client;

import java.net.Socket;

public class Connection {
    private boolean connected;
    private String address;
    private int port;
    private Monitor monitor;
    private Socket socket;

    public Connection(String address, int port, Monitor monitor) {
        this.connected = false;
        this.address = address;
        this.port = port;
        this.monitor = monitor;
    }

    public synchronized void setSocket(Socket clientSocket) {
        this.socket = clientSocket;
        notifyAll();
    }

    public synchronized Socket getClientSocket() {
        try {
            while (null == socket)
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return socket;
    }
}
