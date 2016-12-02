package client;

import server.Read;

public class Client {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        Connection connection = new Connection("localhost", 8765, monitor);
        monitor.putConnection(connection);
        GUI gui = new GUI(monitor);
        new Thread(new Renderer(gui, monitor)).start();
    }
}
