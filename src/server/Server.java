package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        new Thread(new Read(monitor, 8765)).start();
        new Thread(new HttpServer(monitor)).start();
        
        try {
            final int portNumber = 7654;
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Camera HTTP server on port " + portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                monitor.setSocket(clientSocket);
                // - to do: wait (or does next iteration's accept()-call take care of that?)
                //clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
