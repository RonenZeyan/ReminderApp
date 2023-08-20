package com.example.androidfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.androidfinalproject.databasePack.database;

import java.time.LocalDate;
import java.util.Calendar;


public class AddNewTaskFragment extends Fragment {
    private Button datebutton,timebutton,AddTaskButton,CancelButton;
    private static MainViewModel viewModel;
    private EditText tasktit,taskdet; //title,details
    Task task;
    database db;
    DatePickerDialog dpd;
    public static DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog tpd;
    Calendar cal;

    int ho,mi,da,mo,ye;

    private onFragmentAddTaskClickListener listener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onFragmentAddTaskClickListener) //check if context we get is type of onFragmentClickListener, if not we make exception.
            listener= (onFragmentAddTaskClickListener) context;
        else
            throw new ClassCastException("Your activity does not implements onFragmentClickListener");

    }

    //when destroy the fragment then make listener equal to null
    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public AddNewTaskFragment() {
        // Required empty public constructor
    }

    public static AddNewTaskFragment newInstance() {
        AddNewTaskFragment fragment = new AddNewTaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class); //return instance of viewmodel
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_new_task, container, false);
        tasktit=view.findViewById(R.id.TaskNameEditTxt);
        taskdet=view.findViewById(R.id.editTextTextMultiLine);
        timebutton=view.findViewById(R.id.selectTime);
        datebutton=view.findViewById(R.id.selectDateButton);
        CancelButton=view.findViewById(R.id.CancelButton);
        AddTaskButton=view.findViewById(R.id.AddTaskButton);

        datebutton.setOnClickListener(new DateButtonClass());

                onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month+=1;  //we add 1 to month because 1st month is 0 in java
                        String date=day+"/"+month+"/"+year;
                        datebutton.setText(date); //after we get day,month,year we change txt of button to what user set
                        da=day;
                        ye=year;
                        mo=month;
                    }
                };




        timebutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                cal=Calendar.getInstance();
                int hour=cal.get(Calendar.HOUR_OF_DAY);
                ho=hour;
                int min=cal.get(Calendar.MINUTE);
                mi=min;
                //int second=cal.get(Calendar.SECOND);
                tpd=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        ho=hour;
                        mi=min;
                        String time;
                        if(min>9)
                            timebutton.setText(hour+":"+min);
                        else
                            timebutton.setText(hour+":0"+min);
                         time=hour+":"+min;


                    }
                },hour,min,true);
                tpd.show();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFragmentShowInteraction();  //user press cancel then we back to old fragment
            }
        });


    //this listener for add new task to recyclerview
        AddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   db=database.getInstance(getContext());

                String Taskname,details;
                Taskname=tasktit.getText().toString();
                details=taskdet.getText().toString();
                if(Taskname.isEmpty()||details.isEmpty())
                {
                    Toast.makeText(getActivity(), "please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ho>23)
                {
                    ho=ho-23;
                    da+=1;
                }
                task =new Task(Taskname,details,ye,mo,da,ho,mi,"yet");
                addtask(task); //insert the task to the database (not directly, we give it to viewmodel and viewmodel insert it)
                listener.onFragmentShowInteraction(); //comminucate with the activity and tell it we finish in this fragment and the activity decide where to move (and not go to recyclerview fragment from here)
            }
        });
        return view;
    }

    void addtask(Task task){
        viewModel.insertTask(task);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    public interface onFragmentAddTaskClickListener{
        void onFragmentShowInteraction();  //to cominucate with the fragment of display data of item (we send task to display it in the other fragment)
    }


    //inner class for date picker (member class Example)
    class DateButtonClass implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            LocalDate lt = LocalDate.now();
            int year = lt.getYear();
            int month = lt.getMonthValue()-1;
            int day = lt.getDayOfMonth();
            ye=year;
            mo=month;
            da=day;

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Dialog_MinWidth
                    ,onDateSetListener,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
          //  txtDate.setText(day + "-" + (month + 1) + "-" + year);
        }
    }

}