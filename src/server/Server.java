package server;

import java.net.*;
//import java.net.ServerSocket;
//import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        new Thread(new Read(monitor)).start();
        new Thread(new HttpServer(monitor)).start();
        new Thread(new In(monitor)).start();
        new Thread(new Out(monitor)).start();

        try {
            final int portNumber = 8765;
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Camera server on port " + portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                monitor.setSocket(clientSocket);
                // - to do: wait (or does next iteration's accept()-call take
                // care of that?)
                // clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
