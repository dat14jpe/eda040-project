package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

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

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = connection.getSocket();
                InputStream is = socket.getInputStream();

                int packetType = is.read();
                if (Monitor.PACKET_S2C != packetType) {
                    // - what to do?
                }

                int mode = is.read();
                monitor.setMode(mode);

                long timestamp = readLong(is);
                int dataLen = readInt(is);
                byte[] imageData = readBytes(is, dataLen);
                boolean motion = mode == Monitor.MODE_IDLE ? false : true;

                monitor.putImage(new Image(timestamp, id, motion, imageData));
            } catch (Exception e) {
                //throw new Error(e);
            }
        }
    }

    private byte[] readBytes(InputStream is, int n) throws IOException {
        byte[] data = new byte[n];
        int received = 0;
        do {
            received += is.read(data, received, n - received);
        } while (received != n);
        return data;
    }

    private int readInt(InputStream is) throws IOException {
        return ByteBuffer.wrap(readBytes(is, 4)).getInt();
    }

    private long readLong(InputStream is) throws IOException {
        return ByteBuffer.wrap(readBytes(is, 8)).getLong();
    }
}
