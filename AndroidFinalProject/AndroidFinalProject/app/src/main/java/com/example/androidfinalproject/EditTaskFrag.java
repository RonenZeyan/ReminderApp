package com.example.androidfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.zip.Inflater;


public class EditTaskFrag extends Fragment {
    private Button datebutton,timebutton,AddTaskButton,CancelButton;
    private static MainViewModel viewModel;
    private EditText tasktit,taskdet; //title,details
    Task task;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    Calendar cal;
    int ho,mi,da,mo,ye;
    public static DatePickerDialog.OnDateSetListener onDateSetListener;

    private onFragmentEditTaskClickListener listener;
    public EditTaskFrag() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onFragmentEditTaskClickListener) //check if context we get is type of onFragmentClickListener, if not we make exception.
            listener= (onFragmentEditTaskClickListener) context;
        else
            throw new ClassCastException("Your activity does not implements onFragmentClickListener");

    }

    //when destroy the fragment then make listener equal to null
    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public static EditTaskFrag newInstance() {
        EditTaskFrag fragment = new EditTaskFrag();
        return fragment;
    }

    //set task to display it to delete
    public void setTask(Task task)
    {
        this.task=task;
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
        View view= inflater.inflate(R.layout.fragment_edit_task, container, false);
        tasktit=view.findViewById(R.id.editTaskNameEditTxt);
        taskdet=view.findViewById(R.id.editeditTextTextMultiLine);
        timebutton=view.findViewById(R.id.editselectTime);
        datebutton=view.findViewById(R.id.editselectDateButton);
        CancelButton=view.findViewById(R.id.editCancelButton);
        AddTaskButton=view.findViewById(R.id.editAddTaskButton);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class); //return instance of viewmodel
        viewModel.getTaskMutableLiveData().observe(getActivity(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                tasktit.setText(task.getTaskName());
                taskdet.setText(task.getDetails());
                ho=task.getHour(); mi=task.getMin(); ye=task.getYear(); mo=task.getMonth(); da=task.getDay();

                datebutton.setText(task.getDay()+"/"+task.getMonth()+"/"+task.getYear());
                if(task.getMin()>9)  // until min 9 java print 9 and not 09 then we add 0 to the min if small than 9
                {
                    timebutton.setText(task.getHour() + ":" + task.getMin());
                }
                else {
                    timebutton.setText(task.getHour() + ":0" + task.getMin());
                }
            }
        });
        // we make // for this down because we add the mutablelivedata for onchange
//        tasktit.setText(task.getTaskName());
//        taskdet.setText(task.getDetails());
//        ho=task.getHour(); mi=task.getMin(); ye=task.getYear(); mo=task.getMonth(); da=task.getDay();
//
//        datebutton.setText(task.getDay()+"/"+task.getMonth()+"/"+task.getYear());
//        if(task.getHour()>9)  // until min 9 java print 9 and not 09 then we add 0 to the min if small than 9
//        {
//            timebutton.setText(task.getHour() + ":" + task.getMin());
//        }
//        else {
//            timebutton.setText(task.getHour() + ":0" + task.getMin());
//        }
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
                int hour=cal.get(Calendar.HOUR_OF_DAY);  //hour in java is less 3 hours than here in israel
                ho=hour;
                int min=cal.get(Calendar.MINUTE);
                mi=min;
                //int second=cal.get(Calendar.SECOND);
                tpd=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String time=hour+":"+min;
                        if(min>9)
                            timebutton.setText(hour+":"+min);
                        else
                            timebutton.setText(hour+":0"+min);
                        ho=hour;
                        mi=min;
                    }
                },hour,min,true);
                tpd.show();
            }
        });

        AddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ho>23)
                {
                    ho=ho-23;
                    da+=1;
                } //initial new Task and save it
                task.setTaskName(tasktit.getText().toString());
                task.setDetails(taskdet.getText().toString());
                task.setDay(da);
                task.setMonth(mo);
                task.setYear(ye);
                task.setHour(ho);
                task.setMin(mi);
                if(task.getTaskName().isEmpty()||task.getDetails().isEmpty())
                {
                    Toast.makeText(getActivity(), "please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                addtask(task);
                listener.onEditFragmentShowInteraction();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditFragmentShowInteraction();  //user press cancel then we back to old fragment
            }
        });


        return view;
    }

    //replace task to edited one in database (viewmodel give us the ability to comunicate) (in database we set inconflict replace then if task have same id it replace)
    void addtask(Task task){
        viewModel.insertTask(task);
    }

    //used to tell the activity we make the update and press it then can change (not good to replace fragment from here because we didn't want to be in interaction with other fragments)
    public interface onFragmentEditTaskClickListener{
        void onEditFragmentShowInteraction();  //to cominucate with the fragment of display data of item (we send task to display it in the other fragment)
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