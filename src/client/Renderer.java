package client;

import java.util.ArrayList;
import java.util.List;
import common.*;

public class Renderer implements Runnable {
    private GUI gui;
    private Monitor monitor;
    private List<ArrayList<Image>> images;
    private boolean synchronous;
    private long lastSyncSwitch, lastAsyncTime;

    private static final long switchTime = 200L;

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
        long timeToNext = 0; // time until next image should ideally be rendered
        while (true) {
            boolean async = monitor.getMode() == Constants.MODE_IDLE || !synchronous;
            Image image = monitor.getImage(async ? 0 : timeToNext);
            
            if (null != image) {
                int numImages = 0;
                for (List<Image> i : this.images) {
                    numImages += i.size();
                }
                //System.out.println("Images: " + numImages);
            }
            
            long t = System.currentTimeMillis();
            if (async) {
                if (null != image) {
                    if (t - lastAsyncTime < switchTime) {
                        debounceSynchronicity(true);
                    }
                    sendImage(image);
                    lastAsyncTime = t;
                }
            } else { // synchronous mode
                if (null != image) {
                    List<Image> images = this.images.get(image.getCameraId());
                    if (images.isEmpty()) {
                        sendImage(image);
                        timeToNext = 0;
                    } 
                    images.add(image);
                }

                // Show new images.
                timeToNext = Long.MAX_VALUE;
                for (List<Image> images : this.images) {
                    long camTimeToNext = synchronizedMode(images);
                    if (0 != camTimeToNext) {
                        timeToNext = Math.min(camTimeToNext, timeToNext);
                    }
                }
                if (Long.MAX_VALUE == timeToNext) timeToNext = 0;
            }
        }
    }

    private long synchronizedMode(List<Image> images) {
        if (images.size() < 2) return 0;
        long timeToNext = 0;

        long t = System.currentTimeMillis();
        Image lastShown = images.get(0);
        Image nextToShow = images.get(1);
        
        long clientDelay = t - lastShown.getClientTime();
        long serverDelay = nextToShow.getTimestamp() - lastShown.getTimestamp();
        
        if (clientDelay > 1000) {
            System.out.println("Images: " + images.size() + ", client: " + clientDelay + ", server: " + serverDelay);
            System.out.println("Delays between all images: ");
            for (int i = 0; i < images.size() - 1; ++i) {
                Image next = images.get(i + 1), prev = images.get(i);
                System.out.println(i + ": " + (next.getTimestamp() - prev.getTimestamp()));
            }
        }

        // Switch to asynchronous?
        if (clientDelay > switchTime) {
            debounceSynchronicity(false);
        } 
        if (synchronous) {
            final long tolerance = 10;
            do {
                if (images.size() < 2) return timeToNext < 0 ? 0 : timeToNext;
                lastShown = images.get(0);
                nextToShow = images.get(1);
                clientDelay = t - lastShown.getClientTime();
                serverDelay = nextToShow.getTimestamp() - lastShown.getTimestamp();
                
                timeToNext = serverDelay - clientDelay;
                if (timeToNext <= tolerance) {
                    images.remove(0);
                    //while (images.size() > 2) images.remove(0);
                    sendImage(nextToShow);
                }
            } while (timeToNext <= tolerance);
            if (timeToNext < 0) {
                timeToNext = 0;
            }
        }

        return timeToNext;
    }

    private void debounceSynchronicity(boolean synchronous) {
        long t = System.currentTimeMillis();
        final long debounceTime = 3000L;
        if (t - lastSyncSwitch > debounceTime) {
            this.synchronous = synchronous;
            lastSyncSwitch = t;

            if (!synchronous) {
                lastAsyncTime = System.currentTimeMillis();
                for (List<Image> i : images) {
                    i.clear();
                }
            }
        }
    }

    private boolean sendImage(Image image) {
        image.setClientTime(System.currentTimeMillis());
        gui.put(image, synchronous);
        return true;
    }
}
