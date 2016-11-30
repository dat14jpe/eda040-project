package client;

public class Connection {
	private boolean connected;
	private String address;
	private int port;
	private Monitor monitor;
	public Connection(String address, int port, Monitor monitor) {
		this.connected = false;
		this.address = address;
		this.port = port;
		this.monitor = monitor;
	}
}
