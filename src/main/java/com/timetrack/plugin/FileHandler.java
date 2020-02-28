package com.timetrack.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler {
    Logger log = Logger.getInstance("TimeTrack");
    private final File file;

    public FileHandler(File file) {
        this.file = file;
    }

    public void TimeStamp(long currentTime) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file, true),
                        StandardCharsets.UTF_8
                ))
        ) {
            writer.write("\"" +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                            .format(new Date(System.currentTimeMillis())) +
                    "\"" +
                    "," +
                    currentTime +
                    "\n"
            );

        } catch (FileNotFoundException var1) {
            log.error("File not found", var1);
        } catch (UnsupportedEncodingException var2) {
            log.error("Unsupported encoding", var2);
        } catch (IOException var3) {
            log.error("Error writing to file", var3);
        }
    }

    public static String getTextFilePath(Project project) {
        try {
            return project.getProjectFile().getParent().getPath();
        } catch (NullPointerException var5) {
            return project.getProjectFilePath(); //TODO: fix
        }
    }

    public DateHolder getDateHolder() {
        DateHolder dateHolder = new DateHolder();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                log.error("Unable to create project time tracking file, " +
                        file.getAbsolutePath(), ex);
            }
        } else {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))
            ) {
                if (reader.ready()) {
                    String lastLine = "";
                    String currentLine;

                    while ((currentLine = reader.readLine()) != null) {
                        lastLine = currentLine;
                    }

                    int comma = lastLine.indexOf(44);
                    if (comma > -1) {
                        long timeLong = Long.parseLong(lastLine.substring(comma + 1));
                        String dateString = lastLine.substring(1, comma - 1);
                        dateHolder = new DateHolder(timeLong, dateString);
                    }
                }
            } catch (IOException var6) {
                log.error("Can't get Date from file", var6);
            }
        }

        return dateHolder;
    }
}
