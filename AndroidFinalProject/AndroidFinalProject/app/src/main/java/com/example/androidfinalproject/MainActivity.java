package com.example.androidfinalproject;

import static java.lang.System.exit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidfinalproject.Services.MyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements RecyclerViewTasksListFrag.onFragmentClickListener,AddNewTaskFragment.onFragmentAddTaskClickListener,EditTaskFrag.onFragmentEditTaskClickListener {

    SharedPreferences sp;
    SharedPreferences.Editor edit;

    MyReceiver myReceiver;
    private static MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myReceiver=new MyReceiver();
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        edit=sp.edit();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class); //return instance of viewmodel
       // viewModel = new ViewModelProvider(get(MainViewModel.class)); //return instance of viewmodel
        Intent intent = new Intent(getBaseContext(), MyService.class);
        ContextCompat.startForegroundService(getBaseContext(),intent);
        List<Task> tlist=new ArrayList<>();

//        tlist=viewModel.getnearlyTask(23,7,2023);
//        Toast.makeText(this, tlist.get(0).getTaskName()+tlist.get(1).getTaskName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Intent intent = new Intent(getBaseContext(), MyService.class);
       // stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu); //inflate the main menu to the activity
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settingItem:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content ,new MySettingsFragment()).addToBackStack(null)  //myseetingFragment is a class we create here down in MainActivity
                        .commit();
                break; //we can make return true instead of break;

            case R.id.RestoreLastDeletedItem:

                Task deletedTask=new Task("noDeletedTask","",0,0,0,0,0,"yet");
                String lastdeletedTask=sp.getString("deletedTaskName","NoTask");
                if(lastdeletedTask.equals("NoTask")) //in case no deletedTask
                {
                    Toast.makeText(this, "no Deleted Task saved", Toast.LENGTH_LONG).show();
                }
                else  //in case there is a deletedTask
                {
                    deletedTask.setTaskName(lastdeletedTask);
                    deletedTask.setDetails(sp.getString("deletedTaskDetails","nothing"));
                    deletedTask.setStatus(sp.getString("deletedTaskStatus","nothing"));
                    deletedTask.setYear(sp.getInt("deletedTaskYear",0));
                    deletedTask.setMonth(sp.getInt("deletedTaskMonth",0));
                    deletedTask.setDay(sp.getInt("deletedTaskDay",0));
                    deletedTask.setHour(sp.getInt("deletedTaskHour",0));
                    deletedTask.setMin(sp.getInt("deletedTaskMin",0));
                    viewModel.insertTask(deletedTask);
                    edit.clear(); //after we restore the deletedTask we reset the preference
                    edit.commit();
                }
                break;

            case R.id.infoItem:
                Toast.makeText(this, "This app done by Ronen and Adham. to help you mangement your tasks and reminder to your tasks  ", Toast.LENGTH_LONG).show();
                break;

            case R.id.exitItem:
                exit(0);
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////interface for RecyclerViewTasksListFrag fragment /////////////////////////////////////////////////////////////////////////


    @Override
    public void onFragmentShowInteraction(Task task) {
        displayTaskDetailsFrag displayTaskFrag= displayTaskDetailsFrag.newInstance(task); //init the displayTaskDetailsFrag with task to display it
            viewModel.SetTaskMutableLiveData(task);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.FragContainer, displayTaskFrag); //replace change old fragment with new one
            ft.addToBackStack(null); //back to old fragment
            ft.commit(); //commit the transaction

    }

    //activity display Add new Task fragment
    @Override
    public void onClickAddNewTask() {
        AddNewTaskFragment addNewTaskFragment= AddNewTaskFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.FragContainer, addNewTaskFragment); //replace change old fragment with new one
        ft.addToBackStack(null); //back to old fragment
        ft.commit(); //commit the transaction
    }

    //activity display edit task fragment
    @Override
    public void onClickEditTask(Task task) {
        EditTaskFrag editTaskFrag=EditTaskFrag.newInstance();
        editTaskFrag.setTask(task); //send task to edit fragment to edit and enter edited one to database
        viewModel.SetTaskMutableLiveData(task); //change mutableLiveData
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.FragContainer, editTaskFrag); //replace change old fragment with new one
        ft.addToBackStack(null); //back to old fragment
        ft.commit(); //commit the transaction

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////interface for AddNewTaskFragment fragment /////////////////////////////////////////////////////////////////////////


    //interaction between add new task fragment and other fragments
    @Override
    public void onFragmentShowInteraction() {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.FragContainer, RecyclerViewTasksListFrag.newInstance()); //replace change old fragment with new one
            ft.addToBackStack(null); //back to old fragment
            ft.commit(); //commit the transaction


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////interface for editTaskFrag fragment /////////////////////////////////////////////////////////////////////////


    //in this function the fragment tell us it finish the change and update (tell the activity to change screen becasue it finish now)
    //we didn't need to edit task because viewmodel (livedata) help us. we set observe if changes happen we refresh the recyclerview
    @Override
    public void onEditFragmentShowInteraction() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.FragContainer, RecyclerViewTasksListFrag.newInstance()); //replace change old fragment with new one
        ft.addToBackStack(null); //back to old fragment
        ft.commit(); //commit the transaction
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////BroadCast ///////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        super.onStart();
        //we also can do //intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION); //instead of make intentFilter in manifst and set it work all time we make it here to work just when app opened
        registerReceiver(myReceiver,intentFilter); //we give two param (name of class of reciver and IntentFilter var name
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);  //stop Receiver
    }

    //////////////////////////////////////////////////Preference ///////////////////////////////////////////////////////////////

    //this java class for the preference layout for make things in the preference layout  (like any activity it had its class)
    public static class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.setBackgroundColor(Color.WHITE);  //background of preference layout is transparent then we change it to white
        }
    }
}