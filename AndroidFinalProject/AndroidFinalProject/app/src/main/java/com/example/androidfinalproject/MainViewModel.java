package com.example.androidfinalproject;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidfinalproject.databasePack.TaskRepository;
import com.example.androidfinalproject.databasePack.database;
import com.example.androidfinalproject.databasePack.databaseDao;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;
    private LiveData<List<Task>> allTasks;

    private MutableLiveData<Task> selectedCountry;


    public MainViewModel(Application application)
    {
        super(application);
        taskRepository=new TaskRepository(application);
        allTasks= taskRepository.GetAllTasks();
    }
    public LiveData<List<Task>> GetAllTasks(){
        return allTasks;
    }
    void deleteTask(Task task)
    {
        taskRepository.deleteTask(task);
    }
    void insertAllTasks(Task... task)
    {
        taskRepository.insertAllTasks(task);
    }
    void insertTask(Task task)
    {
        taskRepository.insertTask(task);
    }
    List<Task> GetAllTasksForSearch(String name)
    {
        return taskRepository.GetAllTasksForSearch(name);
    }
    List<Task> getnearlyTask(int day1, int month1 , int year1)
    {
        return taskRepository.getnearlyTask(day1,month1,year1);
    }

     MutableLiveData<Task> getTaskMutableLiveData() {
        return taskRepository.getTaskMutableLiveData();
    }

    void SetTaskMutableLiveData(Task task){
        taskRepository.setSelectedTask(task);
    }





}
