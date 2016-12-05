package server;

import common.Image;
import java.net.Socket;

public class Monitor {
    private Image image;
    private Socket clientSocket;
    private int mode;
    private boolean automaticMode;

    public final static int PACKET_S2C = 1, PACKET_C2S = 2;
    public final static int MODE_IDLE = 1, MODE_MOVIE = 2, MODE_AUTO = 3;

    public Monitor() {
        mode = MODE_IDLE;
    }

    // Reset mode between client connections.
    public synchronized void reset() {
        automaticMode = true;
        mode = MODE_IDLE;
    }

    public synchronized int getMode() {
        return mode;
    }

    public synchronized void forceMode(int mode) {
        if (MODE_AUTO == mode) automaticMode = true;
        if (MODE_AUTO != mode) this.mode = mode;
    }

    public synchronized void setMode(int mode) {
        if (automaticMode) this.mode = mode;
    }

    public synchronized void putImage(Image image) {
        this.image = image;

        // Immediately enter movie mode on motion detection.
        if (image.getMotion()) {
            mode = MODE_MOVIE;
        }

        notifyAll();
    }

    public synchronized Image getImage(Image old) {
        try {
            while (old == image)
                wait();
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
            while (null == clientSocket)
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientSocket;
    }
}
