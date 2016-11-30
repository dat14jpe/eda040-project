package client;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Out implements Runnable {
    private Monitor monitor;
    private Connection connection;
    private int id;
    private int mode;

    public Out(Monitor monitor, Connection connection, int id) {
        this.monitor = monitor;
        this.connection = connection;
        this.id = id;
        this.mode = monitor.getMode();
    }
    
    private byte[] toArr(int len, int value) {
        return ByteBuffer.allocate(len).putInt(value).array();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true) {
            Socket socket = connection.getSocket();
            try {
                while (this.mode == monitor.getMode())
                    wait();
                System.out.println("Mode change");
                this.mode = monitor.getMode();
                OutputStream os = socket.getOutputStream();
                os.write(toArr(1, monitor.PACKET_C2S));
                os.write(toArr(1, this.mode));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

}
