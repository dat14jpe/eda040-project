package server;

import java.io.IOException;
import java.net.Socket;

import common.Image;

public class Out implements Runnable {
    private Monitor monitor;

    public Out(Monitor monitor) {
        this.monitor = monitor;
    }

    public void run() {
        Image lastImage = null;
        long timeSent = 0;
        while (true) {
            Image image = monitor.getImage(lastImage);
            lastImage = image;
            Socket socket = monitor.getClientSocket();
            try {
                long timestamp = System.currentTimeMillis();

                // Skip images if in idle mode.
                final long TIME_LIMIT = 5000L;
                if (Monitor.MODE_IDLE == monitor.getMode() && timestamp - timeSent < TIME_LIMIT) {
                    continue;
                }

                int mode = image.getMotion() ? Monitor.MODE_MOVIE : Monitor.MODE_IDLE;
                timeSent = timestamp;
                //System.out.println("SERVER sent image");
                Protocol.writePacket(socket.getOutputStream(), mode, timestamp, image);
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
