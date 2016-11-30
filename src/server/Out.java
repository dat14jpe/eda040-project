package server;
import common.Image;

public class Out implements Runnable {
	private Monitor monitor;
	
	public Out(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public void run() {
		while (true) {
			Image image = monitor.getImage();
			
		}
	}
}
