package client;

import common.Image;
import java.util.ArrayList;

public class Monitor {
    public final static int PACKET_S2C = 1, PACKET_C2S = 2, MODE_IDLE = 1, MODE_MOVIE = 2;
    private int minId;
    private int mode;

    private ArrayList<Image> images;
    private ArrayList<Connection> connections;


    public Monitor() {
        this.minId = 1;
        this.mode = MODE_IDLE;
    }
    public synchronized int getMode() {
        return mode;
    }
    public synchronized void setMode(int mode) {
        this.mode = mode;
        notifyAll();
    }
    public synchronized void putImage(Image image) {
        images.add(image);
        notifyAll();
    }
    public synchronized void putConnection(Connection connection) {
        connections.add(connection);
        new Thread(new In(this, connection, minId)).start();
        new Thread(new Out(this, connection, minId)).start();        
        minId++;
        notifyAll();
    }
    public synchronized ArrayList<Connection> getConnections() {
        return connections;
    }
}
