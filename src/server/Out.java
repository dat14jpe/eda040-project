package server;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import common.Image;

public class Out implements Runnable {
	private Monitor monitor;
	
	public Out(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public void run() {
		Image lastImage = null;
		while (true) {
			Image image = monitor.getImage(lastImage);
			lastImage = image;
			Socket socket = monitor.getClientSocket();
			long timeSent = 0;
			try {
				OutputStream out = socket.getOutputStream();
				int mode = image.getMotion() ? Monitor.MODE_MOVIE : Monitor.MODE_IDLE;
				long timestamp = System.currentTimeMillis();
				
				// Skip images if in idle mode.
				final int TIME_LIMIT = 5000;
				if (Monitor.MODE_IDLE == monitor.getMode() &&
					timestamp - timeSent < TIME_LIMIT) {
					continue;
				}
					
				timeSent = timestamp;
				Protocol.writePacket(out, mode, timestamp, image);
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
	}
}
