package com.timetrack.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileHandler {
    Logger log = Logger.getInstance("TimeTrack");
    private final File file;

    public FileHandler(File file) {
        this.file = file;
    }

    /**
     * Записывает временные метки в файл
     * @param currentTime - миллисекунды записываемые сейчас
     */
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

    /**
     * Читаем из файла построчно, переводим строку в объект класса DateHolder
     * с помощью getDateHolder и добавляем каждый объект в лист dateHolders
     * @return лист с объектами класса DateHolder
     */
    public ArrayList<DateHolder> getDateHoldersFromFile() {
        ArrayList<DateHolder> dateHolders = new ArrayList<>();
        if (!file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException ex) {
                log.error("Unable to create project time tracking file, " +
                        this.file.getAbsolutePath(), ex);
            }
        } else {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))
            ) {
                while (reader.ready()) {
                    dateHolders.add(getDateHolder(reader.readLine()));
                }
            } catch (IOException var6) {
                log.error("", var6);
            }
        }

        return dateHolders;
    }

    /**
     * Парсим строку в объект класса DateHolder
     * @param str - строка файла
     * @return объект класса DateHolder
     */
    public DateHolder getDateHolder(String str) {
        DateHolder dateHolder = new DateHolder();

        int comma = str.indexOf(44);
        if (comma > -1) {
            long timeLong = Long.parseLong(str.substring(comma + 1));
            String dateString = str.substring(1, comma - 1);
            dateHolder = new DateHolder(timeLong, dateString);
        }
        return dateHolder;
    }
}
