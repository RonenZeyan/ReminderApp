package com.example.androidfinalproject.databasePack;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.androidfinalproject.Task;

import java.util.List;


@Dao
public interface databaseDao {

    @Insert(onConflict = REPLACE)  //insert task to the table
    void insertTask(Task task);

    @Insert(onConflict = REPLACE)
    void insertAllTasks(Task... task);  //this (...) mean we enter a set and not just one item

    @Delete  //delete task from the table
    void deleteTask(Task task);

    @Query("SELECT * FROM task_table")  //we set the name of our table task_table
    LiveData<List<Task>> GetAllTasks();

    @Query("SELECT * FROM task_table WHERE TaskName like '%'||:name||'%' ")  //this we search to get all tasks that included the name in taskName field
    List<Task> GetAllTasksForSearch(String name);

    @Query("SELECT * FROM task_table where year<=:year1 And day<=:day1 And month<=:month1")
    List<Task> getnearlyTask(int day1, int month1 , int year1);




}
