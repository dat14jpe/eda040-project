package client;

public class Client {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        Connection connection = new Connection("localhost", 8765, monitor);
        GUI gui = new GUI(monitor);
    }
}
