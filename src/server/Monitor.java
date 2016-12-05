package server;

import common.*;
import java.net.Socket;

public class Monitor {
    private Image image;
    private Socket clientSocket;
    private int mode;
    private boolean automaticMode;

    public Monitor() {
        reset();
    }

    // Reset mode between client connections.
    public synchronized void reset() {
        automaticMode = true;
        mode = Constants.MODE_IDLE;
        notifyAll();
    }

    public synchronized int getMode() {
        return mode;
    }

    public synchronized void forceMode(int mode) {
        automaticMode = Constants.MODE_AUTO == mode;
        if (!automaticMode) this.mode = mode;
        notifyAll();
    }

    public synchronized void setMode(int mode) {
        if (automaticMode) {
            this.mode = mode;
        }
        notifyAll();
    }

    public synchronized void putImage(Image image) {
        this.image = image;

        // Immediately enter movie mode on motion detection.
        if (image.getMotion()) {
            setMode(Constants.MODE_MOVIE);
        } else {
            // Need delay here?
            setMode(Constants.MODE_IDLE);
        }

        notifyAll();
    }

    public synchronized Image getImage(Image old) {
        try {
            while (old == image) wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return image;
    }

    public synchronized void setSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
        notifyAll();
    }

    public synchronized Socket getClientSocket() {
        try {
            while (null == clientSocket || clientSocket.isClosed()) wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientSocket;
    }
}
