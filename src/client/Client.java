package client;

public class Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: two camera addresses required as parameters");
            return;
        }
        Monitor monitor = new Monitor();
        for (int i = 0; i < 2; ++i) {
            Connection connection = new Connection(args[i], 8765, monitor);
            monitor.putConnection(connection);
        }
        GUI gui = new GUI(monitor);
        new Thread(new Renderer(gui, monitor)).start();
    }
}
