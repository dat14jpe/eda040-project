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
            boolean async = monitor.getMode() == Monitor.MODE_IDLE || !synchronous;
            Image image = monitor.getImage(async ? 0 : timeToNext);
            long t = System.currentTimeMillis();
            if (async) {
                if (t - lastAsyncTime < switchTime) {
                    debounceSynchronicity(true);
                }
                gui.put(image);
                lastAsyncTime = t;
            } else { // synchronous mode
                if (null != image) {
                    List<Image> images = this.images.get(image.getCameraId());
                    if (images.isEmpty()) {
                        images.add(image);
                        sendImage(image);
                        timeToNext = 0;
                    } else {
                        images.add(image);
                    }
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
                System.out.println("timeToNext: " + timeToNext);
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

        //System.out.println("Client delay: " + clientDelay + ", server delay: " + serverDelay);

        // Switch to asynchronous?
        if (clientDelay > switchTime) {
            debounceSynchronicity(false);
        } else {
            timeToNext = serverDelay - clientDelay;
            if (timeToNext < 0) {
                timeToNext = 0;
            }
            if (clientDelay >= serverDelay) {
                images.remove(0);
                sendImage(nextToShow);
            }
        }

        return timeToNext;
    }

    private void debounceSynchronicity(boolean synchronous) {
        long t = System.currentTimeMillis();
        final long debounceTime = 3000L;
        if (t - lastSyncSwitch > debounceTime) {
            //System.out.println("Synchronicity switch: " + synchronous);
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
        gui.put(image);
        return true;
    }
}
