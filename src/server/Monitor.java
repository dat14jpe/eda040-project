package server;

public class Monitor {
    // Last image data.
    private byte[] data;
    private int length;

    public Monitor() {

    }

    public synchronized void putImage(byte[] data, int length) {
        this.data = new byte[length];
        System.arraycopy(data, 0, this.data, 0, length);
        this.length = length;
        notifyAll();
    }

    public synchronized byte[] getImage() {
        try {
            while (null == data)
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
