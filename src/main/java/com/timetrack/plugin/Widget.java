package com.timetrack.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

class Widget implements CustomStatusBarWidget, Consumer<MouseEvent> {
    private final TimeTrackThread timeTrackThread;
    private JLabel myLabel = new JLabel("00:00:00");
    private final WidgetPresentation myPresentation;
    private final Logger log = Logger.getInstance("TimeTrack");

    public Widget(TimeTrackThread timeTrackThread, TimeTrack timeTrack) {
        this.timeTrackThread = timeTrackThread;
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
                if (timeTrackThread.isRunning()) {
                    if (e.getClickCount() == 1) {
                        timeTrackThread.setPaused(true);
                        timeTrackThread.setRunning(false);
                        setIcon(timeTrackThread.isPaused());
                        timeTrack.stopThread();
                    }
                } else if (timeTrackThread.isPaused()) {
                    if (e.getClickCount() == 1) {
                        timeTrackThread.setRunning(true);
                        timeTrackThread.setPaused(false);
                        setIcon(timeTrackThread.isPaused());
                        timeTrack.startThread();
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
        this.setIcon(timeTrackThread.isPaused());
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
