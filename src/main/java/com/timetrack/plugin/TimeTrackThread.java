package com.timetrack.plugin;

import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;

class TimeTrackThread extends Thread {
    private final TimeTrack timeTrack;
    private final Logger log = Logger.getInstance("TimeTrack");
    private Object monitor;
    private boolean running;
    private boolean paused;

    public TimeTrackThread(TimeTrack timeTrack) {
        this.timeTrack = timeTrack;
        this.monitor = timeTrack;
    }

    @Override
    public synchronized void start() {
        log.debug("Thread started");
        log.info("Thread started");
        running = true;
        super.start();
    }

    @Override
    public void run() {
        log.debug("Thread run()");

        while(running && !paused && !this.isInterrupted()) {
            synchronized(monitor) {
                try {
                    monitor.wait(1000L);

                    SwingUtilities.invokeLater(() -> {
                        if (running) {
                            timeTrack.setCurrentTime(timeTrack.getCurrentTime() + 1000L);
                            timeTrack.getWidget().getMyLabel()
                                    .setText(TimeFormat.formatToText(timeTrack.getCurrentTime()));
                        }
                    });
                } catch (InterruptedException v) {
                    log.error(v);
                }
            }
        }

    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
