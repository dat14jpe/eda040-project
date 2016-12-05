package client;

import java.util.ArrayList;
import java.util.List;
import common.Image;

public class Renderer implements Runnable {
    
    private GUI gui;
    private Monitor monitor;
    private List<ArrayList<Image>> images;
    private boolean synchronous;
    private long lastSyncSwitch, lastAsyncTime;
    
    public Renderer(GUI gui, Monitor monitor) {
        this.gui = gui;
        this.monitor = monitor;
        images = new ArrayList<ArrayList<Image>>();
        for (int i = 0; i < 2; i++) {
            images.add(new ArrayList<Image>());
        }
        synchronous = true;
    }
    
    @Override
    public void run() {
        final long switchTime = 200L;
        
        long timeToNext = 0; // time until next image should ideally be rendered
        while (true) {
            Image image = monitor.getImage();
            List<Image> images = this.images.get(image.getCameraId());
            long t = System.currentTimeMillis();
            if (monitor.getMode() == Monitor.MODE_IDLE || !synchronous) {
                if (t - lastAsyncTime < switchTime) {
                    debounceSynchronicity(true);
                }
                gui.put(image);
                lastAsyncTime = t;
            } else { // synchronous mode
                if (images.isEmpty()) {
                    images.add(image);
                    sendImage(image);
                } else {
                    images.add(image);
                    Image lastShown = images.get(0);
                    Image nextToShow = images.get(1);
                    long clientDelay = t - lastShown.getClientTime();
                    long serverDelay = nextToShow.getTimestamp() - lastShown.getTimestamp();
                    
                    //System.out.println("Client delay: " + clientDelay + ", server delay: " + serverDelay);
                    
                    // Switch to asynchronous?
                    if (clientDelay > switchTime) {
                        debounceSynchronicity(false);
                        lastAsyncTime = System.currentTimeMillis();
                        for (List<Image> i : this.images) {
                            i.clear();
                        }
                    } else {
                        timeToNext = serverDelay - clientDelay;
                        if (clientDelay >= serverDelay) {
                            images.remove(0);
                            sendImage(nextToShow);
                        }
                    }
                }
            }
        }

    }
    
    private void debounceSynchronicity(boolean synchronous) {
        long t = System.currentTimeMillis();
        final long debounceTime = 3000L;
        if (false)
        if (t - lastSyncSwitch > debounceTime) {
            System.out.println("Synchronicity switch: " + synchronous);
            this.synchronous = synchronous;
            lastSyncSwitch = t;
        } 
    }
    
    private boolean sendImage(Image image) {
        image.setClientTime(System.currentTimeMillis());
        gui.put(image);
        return true;
    }

}
