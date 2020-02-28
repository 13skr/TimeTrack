package com.timetrack.plugin;

import java.util.concurrent.TimeUnit;

public class TimeFormat {
    public static final long MONTH = 2592000000L;
    public static final long DAY = 86400000L;
    public static final long HOUR = 3600000L;
    public static final long MINUTE = 60000L;
    public static final long SEC = 1000L;

    public TimeFormat() {
    }

    /**
     * Переводит миллисекунды в формат секундомера в виде строки
     * @param l - миллисекунды
     * @return время в текстовом формате
     */
    public static String formatToText(long l) {
        if (l >= 0) {
            return l <= 86400000L ?
                    String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(l),
                    TimeUnit.MILLISECONDS.toMinutes(l) %
                            TimeUnit.HOURS.toMinutes(1L),
                    TimeUnit.MILLISECONDS.toSeconds(l) %
                            TimeUnit.MINUTES.toSeconds(1L))
                    :
                    ">1 day";
        } else {
            System.err.println("Failed to format, < 0");
            return "00:00:00";
        }
    }
}
