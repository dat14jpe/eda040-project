package client;

import common.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Monitor {
    private int minId;
    private int mode;
    private boolean automaticMode; // true if mode can be changed by setMode
    private long movieTime;
    
    private int lastMotionId;
    boolean motion;

    private Queue<Image> images;
    private ArrayList<Connection> connections;


    public Monitor() {
        connections = new ArrayList<Connection>();
        this.minId = 0;
        this.mode = Constants.MODE_IDLE;
        images = new LinkedList<Image>();
        lastMotionId = 0;
        motion = false;
        
        automaticMode = true;
    }

    public synchronized int getMode() {
        return mode;
    }

    public synchronized int getForcedMode() {
        return automaticMode ? Constants.MODE_AUTO : mode;
    }

    public synchronized void forceMode(int mode) {
        if (Constants.MODE_AUTO != mode) {
            this.mode = mode;
        }
        automaticMode = Constants.MODE_AUTO == mode;
        movieTime = 0;
        notifyAll();
    }

    public synchronized void setMode(int mode) {
        if (automaticMode) {
            long t = System.currentTimeMillis();
            if (Constants.MODE_MOVIE == mode) {
                this.mode = Constants.MODE_MOVIE;
                movieTime = t;
            } else if (t - movieTime > Constants.MOVIE_TIME) {
                this.mode = mode;
            }
        }
        notifyAll();
    }

    public synchronized int waitForModeChange() {
        int latestMode = mode;
        boolean wasAutomatic = automaticMode, wasMotion = motion;
        while (latestMode == mode && wasAutomatic == automaticMode) {
            if (automaticMode && wasMotion != motion) {
                break;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(automaticMode + " " + mode + " (was " + wasAutomatic + " " + latestMode + ")");
        if (automaticMode) {
            //return mode == Constants.MODE_IDLE ? Constants.MODE_AUTO : Constants.MODE_AUTO_MOVIE;
            return motion ? Constants.MODE_MOVIE : Constants.MODE_AUTO;
        } else {
            return mode;
        }
    }

    public synchronized void putImage(Image image) {
        if (image.getMotion()) {
            lastMotionId = image.getCameraId();
            motion = true;
        } else if (image.getCameraId() == lastMotionId) {
            motion = false;
        }
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
        if (!images.isEmpty()) return images.poll();
        try {
            if (0 == timeout) {
                while (images.isEmpty()) wait();
            } else {
                wait(timeout);
                if (images.isEmpty()) return null;
                return images.poll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return images.poll();
    }
}
