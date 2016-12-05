package client;

import common.Image;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Monitor {
    public final static int PACKET_S2C = 1, PACKET_C2S = 2;
    public final static int MODE_IDLE = 1, MODE_MOVIE = 2, MODE_AUTO = 3;
    public final static long MOVIE_TIME = 3000;

    private int minId;
    private int mode;
    private boolean automaticMode; // true if mode can be changed by setMode
    private long movieTime;

    private Queue<Image> images;
    private ArrayList<Connection> connections;


    public Monitor() {
        connections = new ArrayList<Connection>();
        this.minId = 0;
        this.mode = MODE_IDLE;
        images = new LinkedList<Image>();
    }

    public synchronized int getMode() {
        return mode;
    }

    public synchronized int getForcedMode() {
        return automaticMode ? MODE_AUTO : mode;
    }

    public synchronized void forceMode(int mode) {
        if (MODE_AUTO != mode) {
            this.mode = mode;
        }
        automaticMode = MODE_AUTO == mode;
        movieTime = 0;
        notifyAll();
    }

    public synchronized void setMode(int mode) {
        if (automaticMode) {
            long t = System.currentTimeMillis();
            if (MODE_MOVIE == mode) {
                this.mode = MODE_MOVIE;
                movieTime = t;
            } else if (t - movieTime > MOVIE_TIME) {
                this.mode = mode;
            }
        }
        notifyAll();
    }

    public synchronized int waitForModeChange() {
        int latestMode = mode;
        while (latestMode == mode) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mode;
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

    public synchronized Image getImage() {
        return getImage(0);
    }

    // Returns null on timeout.
    public synchronized Image getImage(long timeout) {
        try {
            while (images.isEmpty()) wait(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (images.isEmpty()) {
            return null;
        }
        return images.poll();
    }
}
