package server;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class Read implements Runnable {
	private Monitor monitor;
	private AxisM3006V camera;
	private int port;
	
	public Read(Monitor monitor, int port) {
		this.monitor = monitor;
		this.port = port;
		camera = new AxisM3006V();
		camera.init();
		camera.setProxy("argus-1.student.lth.se", 5555);
		camera.connect();
	}
	
	public void run() {
		byte[] data = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		while (true) {
			int length = camera.getJPEG(data, 0);
			boolean motion = camera.motionDetected();
			monitor.putImage(data, length);
		}
	}
}
