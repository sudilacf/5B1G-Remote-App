package com.fish.feeder.model;

public class History {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private long epoch_time;
    private String week_day_name;
    private String month_name;
    private String meridian;

    public History() {

    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public long getEpoch_time() {
        return epoch_time;
    }

    public void setEpoch_time(long epoch_time) {
        this.epoch_time = epoch_time;
    }

    public String getWeek_day_name() {
        return week_day_name;
    }

    public void setWeek_day_name(String week_day_name) {
        this.week_day_name = week_day_name;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public String getMeridian() {
        return meridian;
    }

    public void setMeridian(String meridian) {
        this.meridian = meridian;
    }
}
