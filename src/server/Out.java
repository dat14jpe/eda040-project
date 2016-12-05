package server;

import java.io.IOException;
import java.net.Socket;

import common.*;

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
                if (Constants.MODE_IDLE == monitor.getMode() && timestamp - timeSent < TIME_LIMIT) {
                    continue;
                }

                int mode = image.getMotion() ? Constants.MODE_MOVIE : Constants.MODE_IDLE;
                timeSent = timestamp;
                Protocol.writePacket(socket.getOutputStream(), mode, timestamp, image);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
