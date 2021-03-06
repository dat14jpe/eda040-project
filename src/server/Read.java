package server;

import common.Image;
import se.lth.cs.eda040.realcamera.AxisM3006V;

public class Read implements Runnable {
    private Monitor monitor;
    private AxisM3006V camera;

    public Read(Monitor monitor) {
        this.monitor = monitor;
        camera = new AxisM3006V();
        camera.init();
        camera.setProxy("argus-3.student.lth.se", 5555);
        camera.connect();
    }

    public void run() {
        final int BUFFER_SIZE = AxisM3006V.IMAGE_BUFFER_SIZE;
        byte[] data = new byte[BUFFER_SIZE];

        while (true) {
            int length = camera.getJPEG(data, 0);
            boolean motion = camera.motionDetected();
            byte[] imgData = new byte[length];
            System.arraycopy(data, 0, imgData, 0, length);
            monitor.putImage(new Image(System.currentTimeMillis(), 0, motion, imgData));
        }
    }
}
