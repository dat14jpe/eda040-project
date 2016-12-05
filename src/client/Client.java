package client;

public class Client {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        for (int i = 0; i < 2; ++i) {
            Connection connection = new Connection("argus-" + (1 + i), 8765, monitor);
            monitor.putConnection(connection);
        }
        GUI gui = new GUI(monitor);
        new Thread(new Renderer(gui, monitor)).start();
    }
}
