package server;

import java.io.IOException;
import java.net.*;
//import java.net.ServerSocket;
//import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        final int httpPort = 7654, port = 8765;

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
                    System.out.println("one");
                    monitor.setSocket(serverSocket.accept());
                    System.out.println("one point five");
                    // - to do: wait (or does next iteration's accept()-call take
                    // care of that?)
                } catch (Exception e) {
                    System.out.println("two");
                }
            }
        } catch (Exception e) {
            System.out.println("three");
            e.printStackTrace();
        } finally {
            System.out.println("four");
            if (serverSocket != null) {
                System.out.println("five");
                try {
                    System.out.println("six");
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("seven");
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Server exiting");
    }
}
