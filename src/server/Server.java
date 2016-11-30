package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
                //clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
