package server;

public class Server {
	public static void main(String[] args) {
		Monitor monitor = new Monitor();
		new Thread(new Read(monitor, 8765)).start();
		new Thread(new HttpServer(monitor)).start();
	}
}
