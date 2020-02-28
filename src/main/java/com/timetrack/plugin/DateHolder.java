package com.timetrack.plugin;

public class DateHolder {
    private long time;
    private String date;

    public DateHolder(long time, String date) {
        this.time = time;
        this.date = date;
    }

    public DateHolder(){

    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
