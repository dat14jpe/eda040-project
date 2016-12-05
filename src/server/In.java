package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import common.Constants;

public class In implements Runnable {
    private Monitor monitor;

    public In(Monitor monitor) {
        this.monitor = monitor;
    }

    public void run() {
        while (true) {
            Socket socket = monitor.getClientSocket();
            try {
                InputStream in = socket.getInputStream();
                int packetType = in.read();
                if (Constants.PACKET_C2S != packetType) {
                    // - log error?
                    continue;
                }
                int mode = in.read();
                monitor.forceMode(mode);
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
