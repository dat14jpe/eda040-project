package client;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import common.Image;

public class In implements Runnable {
    private Monitor monitor;
    private Connection connection;
    private int id;

    public In(Monitor monitor, Connection connection, int id) {
        this.monitor = monitor;
        this.connection = connection;
        this.id = id;
    }

    public int toInt(byte[] arr) {
        return ByteBuffer.wrap(arr).getInt();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = connection.getSocket();
                
                InputStream is = socket.getInputStream();

                int packetType = is.read();

                int mode = is.read();
                monitor.setMode(mode);

                byte[] timestamp = new byte[8];
                is.read(timestamp);

                byte[] dataLen = new byte[4];
                is.read(dataLen);

                byte[] imageData = new byte[toInt(dataLen)];
                is.read(imageData);
                
                monitor.putImage(new Image(toInt(timestamp), this.id, imageData, mode == monitor.MODE_IDLE ? false : true));
            } catch (Exception e) {
                //throw new Error(e);
            }
        }
    }

}
