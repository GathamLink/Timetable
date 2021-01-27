package com.example.timetable.Entity;

import java.io.Serializable;

public class ListItem implements Serializable {

    private int id;
    private String title, date, time, info;

    public ListItem(int id, String title, String date, String time, String info) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.info = info;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
