package com.example.androidfinalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link displayTaskDetailsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class displayTaskDetailsFrag extends Fragment {


    //in this fragment user can get details about a item chosen in the recyclerView List
    //which mean if user choose a item from list in RecyclerViewTasksListFrag he will be promoted to this fragment
    //to display all the details of the Item
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "task";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Task task;

    private TextView tasktitleTV,taskdescTV,dateTV,TimeTV;
    private TextView tasktitlePrint,taskdescprint,dateprint,Timeprint;
    private static MainViewModel viewModel;


    public displayTaskDetailsFrag() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static displayTaskDetailsFrag newInstance(Task task) {
        Bundle args = new Bundle();
        displayTaskDetailsFrag fragment= new displayTaskDetailsFrag();
        fragment.setArguments(args);
        fragment.setTask(task);
        return fragment;
    }

    //we called this method from newInstance
    public void setTask(Task task)
    {
        this.task=task;  //we save the task sended from the activity
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      if (getArguments() != null) {
   //         mParam1 = getArguments().getString(ARG_PARAM1);
    //    }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //we also can inflate this items in onViewCreated
        View v= inflater.inflate(R.layout.fragment_display_task_details, container, false);
        tasktitleTV=v.findViewById(R.id.taskTitleTxtview);
        tasktitlePrint=v.findViewById(R.id.TaskTitlePrint);
        taskdescTV=v.findViewById(R.id.TaskdescTxtView);
        taskdescprint=v.findViewById(R.id.TaskDetailsPrint);
        dateTV=v.findViewById(R.id.dateTxtView);
        dateprint=v.findViewById(R.id.TaskDatePrint);
        TimeTV=v.findViewById(R.id.timeTxtView);
        Timeprint=v.findViewById(R.id.TaskTimePrint);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class); //return instance of viewmodel
        viewModel.getTaskMutableLiveData().observe(getActivity(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
           //     Toast.makeText(getActivity(), "display task work ", Toast.LENGTH_SHORT).show();
                    tasktitlePrint.setText(task.getTaskName());
                    taskdescprint.setText(task.getDetails());
                    dateprint.setText( task.getDay()+"/"+task.getMonth()+"/"+task.getYear());
                    if(task.getMin()>9)  // until min 9 java print 9 and not 09 then we add 0 to the min if small than 9
                    {
                        Timeprint.setText(task.getHour()+":"+task.getMin());
                    }
                    else {
                        Timeprint.setText(task.getHour()+":0"+task.getMin());
                    }
                }
        });

        //we get from the activity the task object and set his data in our widgets
//        tasktitlePrint.setText(task.getTaskName());
//        taskdescprint.setText(task.getDetails());
//        dateprint.setText( task.getDay()+"/"+task.getMonth()+"/"+task.getYear());
//        if(task.getHour()>9)  // until min 9 java print 9 and not 09 then we add 0 to the min if small than 9
//        {
//            Timeprint.setText(task.getHour()+":"+task.getMin());
//        }
//        else {
//            Timeprint.setText(task.getHour()+":0"+task.getMin());
//        }

        return v;
    }


}