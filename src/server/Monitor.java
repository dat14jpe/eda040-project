package server;
import common.Image;
import java.net.Socket;

public class Monitor {
	private Image image;
	private Socket clientSocket;

    public Monitor() {

    }

    public synchronized void putImage(Image image) {
    	this.image = image;
        notifyAll();
    }

    public synchronized Image getImage() {
        try {
            while (null == image) wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return image;
    }
    
    public synchronized void setSocket(Socket clientSocket) {
    	
    }
}
