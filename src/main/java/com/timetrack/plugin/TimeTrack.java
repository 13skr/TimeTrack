package com.timetrack.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeTrack implements ProjectComponent {
    private StatusBar statusBar;
    private TimeTrack.Widget myWidget;
    private TimeTrack.TimeTrackThread timeTrackThread;
    private ArrayList<DateHolder> dateHolders = new ArrayList<>();
    private Project currentProject;
    private long nonActivity;
    private long currentTime;
    private boolean running;
    private boolean paused;
    private FileHandler fileHandler;
    private Logger log = Logger.getInstance("TimeTrack");

    public TimeTrack(Project project) {
        this.currentProject = project;
        this.running = true;
        this.paused = false;
    }

    @Override
    public void projectOpened() {
        this.fileHandler = new FileHandler(new File(FileHandler.getTextFilePath(this.currentProject),
                this.currentProject.getName() + ".time"));
        dateHolders = fileHandler.getDateHoldersFromFile();
        currentTime = (dateHolders.size() > 0) ? dateHolders.get(dateHolders.size() - 1).getTime() : 0L;
        statusBar = WindowManager.getInstance().getStatusBar(currentProject);
        myWidget = new TimeTrack.Widget();
        statusBar.addWidget(myWidget, "before FatalError");
        startThread();
        log.info("project opened at  " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS")
                .format(new Date(System.currentTimeMillis())));
    }

    @Override
    public void projectClosed() {
        updateTime();
        statusBar.removeWidget(myWidget.ID());
    }

    private void startThread() {
        timeTrackThread = new TimeTrack.TimeTrackThread(this);
        timeTrackThread.start();
    }

    /**
     * Обновляем временную метку
     */
    private void updateTime() {
        fileHandler.TimeStamp(currentTime);
    }

    private void stopThread() {
        timeTrackThread.halt();
        synchronized (this) {
            this.notify();
        }
    }

    private class TimeTrackThread extends Thread {
        private Object monitor;

        public TimeTrackThread(TimeTrack timeTrack) {
            this.monitor = timeTrack;
        }

        @Override
        public synchronized void start() {
            TimeTrack.this.log.debug("Thread started");
            TimeTrack.this.log.info("Thread started");
            TimeTrack.this.running = true;
            super.start();
        }

        @Override
        public void run() {
            log.debug("Thread run()");

            while(running && !paused && !this.isInterrupted()) {
                synchronized(monitor) {
                    try {
                        monitor.wait(1000L);

//                        //TODO: add nonActivity pause
//                        nonActivity = 1000L;
//                        if (nonActivity > 300000L && !paused) {
//                            paused = true;
//                            myWidget.setIcon(TimeTrack.this.paused);
//                            this.halt();
//                        }

                        SwingUtilities.invokeLater(() -> {
                            if (running) {
                                currentTime += 1000L;
                                TimeTrack.this.myWidget.getMyLabel()
                                        .setText(TimeFormat.formatToText(TimeTrack.this.currentTime));
                            }
                        });
                    } catch (InterruptedException v) {

                    }
                }
            }

        }

        /**
         * Убиваем поток и сохраняем время
         */
        public void halt() {
            log.info("Thread halt()");
            running = false;
            updateTime();
        }

    }

    private class Widget implements CustomStatusBarWidget, Consumer<MouseEvent> {
        private JLabel myLabel = new JLabel("00:00:00");
        private final WidgetPresentation myPresentation;

        public Widget() {
            myLabel.setToolTipText("Click to pause or run");
            myPresentation = new WidgetPresentation() {
                @Nullable
                @Override
                public String getTooltipText() {
                    return "ClockWidget";
                }

                @Nullable
                @Override
                public Consumer<MouseEvent> getClickConsumer() {
                    return Widget.this;
                }
            };
            this.myLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (TimeTrack.this.running) {
                        if (e.getClickCount() == 1) {
                            TimeTrack.this.paused = true;
                            TimeTrack.this.running = false;
                            myWidget.setIcon(TimeTrack.this.paused);
                            stopThread();
                        }
                    } else if (TimeTrack.this.paused) {
                        if (e.getClickCount() == 1) {
                            TimeTrack.this.running = true;
                            TimeTrack.this.paused = false;
                            myWidget.setIcon(TimeTrack.this.paused);
                            startThread();
                        }
                    }
                }
            });

        }

        public JLabel getMyLabel() {
            return this.myLabel;
        }

        @Override
        public JComponent getComponent() {
            this.setIcon(TimeTrack.this.paused);
            return myLabel;
        }

        @NotNull
        @Override
        public String ID() {
            return "Clock ID";
        }

        @Nullable
        @Override
        public WidgetPresentation getPresentation(@NotNull PlatformType type) {
            return null;
        }

        @Override
        public void install(@NotNull StatusBar statusBar) {

        }

        @Override
        public void dispose() {

        }

        @Override
        public void consume(MouseEvent mouseEvent) {

        }

        public void setIcon(boolean paused) {
            if (paused) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        BufferedImage img = ImageIO.read(getClass()
                                .getResource("/images/paused.png"));
                        if (img != null) {
                            myLabel.setIcon(new ImageIcon(img.getScaledInstance(16, 16, 2)));
                        }
                    } catch (IOException exp) {
                        log.error("pause image not found");
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    try {
                        BufferedImage img = ImageIO.read(getClass()
                                .getResource("/images/running.png"));
                        if (img != null) {
                            myLabel.setIcon(new ImageIcon(img.getScaledInstance(16,16,2)));
                        }
                    } catch (IOException exp) {
                        log.error("run image not found");
                    }
                });
            }
        }
    }
}
