package com.example.androidfinalproject.databasePack;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidfinalproject.Task;

import java.util.List;

public class TaskRepository {

    private databaseDao databasedao;
    private LiveData<List<Task>> allTasks;

    private MutableLiveData<Task> selectedCountry;
    public TaskRepository(Application application) {
        database db = database.getInstance(application);
        databasedao = db.databasedao();
        allTasks = databasedao.GetAllTasks();
        selectedCountry=new MutableLiveData<>();
    }

        public LiveData<List<Task>> GetAllTasks(){
            return allTasks;
        }
        public void deleteTask(Task task)
        {
          databasedao.deleteTask(task);
        }
        public void insertAllTasks(Task... task)
        {
            databasedao.insertAllTasks(task);
        }
        public void insertTask(Task task)
        {
            databasedao.insertTask(task);
        }
        public List<Task> GetAllTasksForSearch(String name)
        {
            return databasedao.GetAllTasksForSearch(name);
        }
        public List<Task> getnearlyTask(int day1, int month1 , int year1)
        {
          return databasedao.getnearlyTask(day1,month1,year1);
        }

        public MutableLiveData<Task> getTaskMutableLiveData() {
        return selectedCountry;
        }

        public void setSelectedTask(Task task) {
        selectedCountry.setValue(task);
        }
    }



