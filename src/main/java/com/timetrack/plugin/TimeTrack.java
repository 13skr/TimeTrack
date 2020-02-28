package com.timetrack.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTrack implements ProjectComponent {
    private StatusBar statusBar;
    private Widget widget;
    private TimeTrackThread timeTrackThread;
    private DateHolder dateHolder;
    private Project currentProject;
    private long currentTime;

    private FileHandler fileHandler;
    private Logger log = Logger.getInstance("TimeTrack");

    public TimeTrack(Project project) {
        this.currentProject = project;
    }

    @Override
    public void projectOpened() {
        timeTrackThread = new TimeTrackThread(this);
        timeTrackThread.setRunning(true);
        timeTrackThread.setPaused(false);

        this.fileHandler = new FileHandler(new File(FileHandler.getTextFilePath(this.currentProject),
                this.currentProject.getName() + ".time"));

        dateHolder = fileHandler.getDateHolder();
        currentTime = (dateHolder.getTime() > 0) ? dateHolder.getTime() : 0L;

        statusBar = WindowManager.getInstance().getStatusBar(currentProject);
        widget = new Widget(timeTrackThread, this);
        statusBar.addWidget(widget, "before FatalError");

        timeTrackThread.start();

        log.info("project opened at  " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS")
                .format(new Date(System.currentTimeMillis())));
    }

    @Override
    public void projectClosed() {
        updateTime();
        statusBar.removeWidget(widget.ID());
    }

    public void updateTime() {
        fileHandler.TimeStamp(currentTime);
    }

    public void startThread() {
        timeTrackThread = new TimeTrackThread(this);
        timeTrackThread.start();
    }

    public void stopThread() {
        log.info("Thread halt()");
        timeTrackThread.setRunning(false);
        updateTime();
        synchronized (this) {
            this.notify();
        }
    }

    public Widget getWidget() {
        return widget;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
