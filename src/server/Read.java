package server;

import java.util.ArrayList;
import java.util.List;

import common.Image;
import se.lth.cs.eda040.fakecamera.AxisM3006V;

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
        
        long startT = System.currentTimeMillis();
        int n = 0;
        long lastT = System.currentTimeMillis();
        
        while (true) {
            int length = camera.getJPEG(data, 0);
            
            ++n;
            long t = System.currentTimeMillis();
            //System.out.println("Delay: " + (t - lastT));
            //System.out.println("FPS: " + 1000.0 / (t - lastT));
            //System.out.println("FPS: " + 1000.0 * n / (t - startT));
            lastT = t;
            
            boolean motion = camera.motionDetected();
            byte[] imgData = new byte[length];
            System.arraycopy(data, 0, imgData, 0, length);        
            monitor.putImage(new Image(System.currentTimeMillis(), 0, motion, imgData));
        }
    }
}
