package client;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Out implements Runnable {
    private Monitor monitor;
    private Connection connection;
    private int id;

    public Out(Monitor monitor, Connection connection, int id) {
        this.monitor = monitor;
        this.connection = connection;
        this.id = id;
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
                int mode = monitor.waitForModeChange();
                System.out.println("Mode change");          
                OutputStream os = socket.getOutputStream();
                os.write(monitor.PACKET_C2S);
                os.write(mode);
            } catch (Exception e) {
                //throw new Error(e);
            }
        }
    }

}
