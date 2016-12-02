package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
                if (Monitor.PACKET_C2S != packetType) {
                    //System.out.println("Server error: wrong packet type (" + packetType + ")");
                    continue;
                }
                int mode = in.read();
                System.out.println("server in mode: " + mode);
                monitor.setMode(mode);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
}
