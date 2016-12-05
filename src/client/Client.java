package client;

public class Client {
    public static void main(String[] args) {
        final int defaultPort = 8765;
        int numCameras = args.length;
        if (numCameras < 1) {
            // Use default value.
            args = new String[1];
            args[0] = "localhost";
            numCameras = 1;
        }
        
        Monitor monitor = new Monitor();
        for (int i = 0; i < numCameras && i < 2; ++i) {
            String[] split = args[i].split(":");
            int port = split.length > 1 ? Integer.parseInt(split[1]) : defaultPort;
            Connection connection = new Connection(split[0], port, monitor);
            if (!connection.isConnected()) {
                System.out.println("Could not connect to " + split[0] + ":" + port);
            }
            monitor.putConnection(connection);
        }
        
        GUI gui = new GUI(monitor);
        new Thread(new Renderer(gui, monitor)).start();
    }
}
