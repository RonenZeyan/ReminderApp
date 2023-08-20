package com.example.androidfinalproject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Comparator;

@Entity(tableName = "task_table")
public class Task {

    @NonNull
    @ColumnInfo(name = "TaskName")
    String TaskName;
    @ColumnInfo(name = "Details")
    String Details;
    @ColumnInfo(name = "ID")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    long ID;
    @ColumnInfo(name = "status")
    @NonNull
    String status;
    @ColumnInfo(name = "year")
    @NonNull
    int year;
    @ColumnInfo(name = "month")
    int month;
    @ColumnInfo(name = "day")
    int day;
    @ColumnInfo(name = "hour")
    int hour;
    @ColumnInfo(name = "min")
    int min;

    public Task(String TaskName, String Details, int year, int month, int day, int hour, int min, String status) {
        this.TaskName = TaskName;
        this.Details = Details;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.status=status;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String title) {
        TaskName = title;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Comparator<Task> sortBy(final String sortType) {
        return new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                    if (sortType.equals("SortByDate")) {
                    // Compare the due dates
                    Calendar c1 = Calendar.getInstance();
                    c1.set(t1.getYear(), t1.getMonth(), t1.getDay());
                    Calendar c2 = Calendar.getInstance();
                    c2.set(t2.getYear(), t2.getMonth(), t2.getDay());
                    return c1.compareTo(c2);
                    }
                    if(sortType.equals("SortByID")) {
                        if(t1.getID()==t2.getID()) return 0;
                        if(t1.getID()>t2.getID()) return 1;
                        if(t1.getID()<t2.getID()) return 2;

                    }
                       return 0;
                    }


            };
    }

}
