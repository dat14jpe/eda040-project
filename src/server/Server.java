package server;

import java.io.IOException;
import java.net.*;
//import java.net.ServerSocket;
//import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        // Optional parameters: "main" port, and HTTP port.
        // Defaults are given below.
        int port = 8765, httpPort = 7654;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) httpPort = Integer.parseInt(args[1]);

        Monitor monitor = new Monitor();
        new Thread(new Read(monitor)).start();
        new Thread(new HttpServer(monitor, httpPort)).start();
        new Thread(new In(monitor)).start();
        new Thread(new Out(monitor)).start();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Camera server on port " + port);
            while (true) {
                monitor.reset();
                try {
                    monitor.setSocket(serverSocket.accept());
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Server exiting");
    }
}
