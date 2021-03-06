package client;

import java.io.OutputStream;
import java.net.Socket;

import common.Constants;

public class Out implements Runnable {
    private Monitor monitor;
    private Connection connection;

    public Out(Monitor monitor, Connection connection, int id) {
        this.monitor = monitor;
        this.connection = connection;
        //this.id = id;
    }

    @Override
    public void run() {
        while(true) {
            Socket socket = connection.getSocket();
            try {
                int mode = monitor.waitForModeChange();
                //System.out.println("Mode change (" + mode + ", " + monitor.getMode() + ")");
                OutputStream os = socket.getOutputStream();
                os.write(Constants.PACKET_C2S);
                os.write(mode);
            } catch (Exception e) {
                //throw new Error(e);
            }
        }
    }

}
