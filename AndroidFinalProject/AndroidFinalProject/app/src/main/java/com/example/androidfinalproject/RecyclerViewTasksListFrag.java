package com.example.androidfinalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidfinalproject.databasePack.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RecyclerViewTasksListFrag extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener   {

    List<Task> tasksListgettenLive;
    RecyclerView recyclerView;
    Button addnewTaskButt;
    boolean flag=false;

    TaskRVadapter adapter;

    SharedPreferences sp;
    SharedPreferences.Editor edit;


    private static MainViewModel viewModel;

    private onFragmentClickListener listener;  //we use this listener to camenucate from fragment to activity


    //this method is the first method that the fragment read it, it get pointer to the activity that start this method
    //but the pointer in type context then we first need to casting to interface type then use it
    //(we need this method to use the interface we implement in the activity to can comminucate with other fragments )
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onFragmentClickListener) //check if context we get is type of onFragmentClickListener that mainactivity implement it , if not we make exception.
            listener= (onFragmentClickListener) context;
        else
            throw new ClassCastException("Your activity does not implements onFragmentClickListener"); //in case activity didn't implement our interface get this exception description

    }

    //when destroy the fragment then make listener equal to null
    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public RecyclerViewTasksListFrag() {
        // Required empty public constructor
    }

    //this menu is just the search icon (we set one in the activity to display it for all fragments and another one is here
    // to display the search icon just in this fragment specially and not in other fragments
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recyclersearchmenu, menu); //inflate the test menu
        SearchView searchview = (SearchView) menu.findItem(R.id.mainsearch).getActionView();  //inflate search icon and this to set listener to this icon
        searchview.setSubmitButtonEnabled(true);  //to display a arrow in the search (this arrow mean submit i want to search)
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //this two methods have two option. in first method the search operation done after we press the arrow we set in searchview.setSubmitButtonEnabled(true);
            //the second method (onQueryTextChange) the search operation start after you press one char
            @Override
            public boolean onQueryTextSubmit(String query) {
                //ArrayList<Task> taskarraylist=new ArrayList<>();
                List<Task> tasklist=new ArrayList<>();
                tasklist=viewModel.GetAllTasksForSearch(query);
                adapter.setTasks((ArrayList)tasklist);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //here what we do after we press x witch mean finish search (we returned the old list before search operation)
        searchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.setTasks((ArrayList)tasksListgettenLive);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public static RecyclerViewTasksListFrag newInstance() {
        RecyclerViewTasksListFrag fragment = new RecyclerViewTasksListFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        tasksListgettenLive=new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view= inflater.inflate(R.layout.fragment_recycler_view_tasks_list, container, false);
       // addnewTaskButt=view.findViewById(R.id.AddnewTaskBut);  //inflate the fragment_recycler_view_tasks_list
        recyclerView=view.findViewById(R.id.rv);
        sp= PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(this);
        edit = sp.edit();
        ArrayList<Task> task=new ArrayList<>();
        adapter= new TaskRVadapter(task, new OnItemClickListener() {
            @Override //when user enter in item in recyclerview the onItemClick method works
            public void OnItemClick(Task task) {
                // Toast.makeText(getContext(), task.getTitle(), Toast.LENGTH_SHORT).show();
                listener.onFragmentShowInteraction(task); //here we use the method we implement in the activity
                //in the protocol of send from fragment to fragment we should send to activity then activity send (עושים את זה כדי לא להיות קשורים בפרגמנטים אחרים)
            }

            @Override
            public void onItemLongClick(Task task) {
                viewModel.deleteTask(task);
           //     sp= PreferenceManager.getDefaultSharedPreferences(getContext());
           //     edit = sp.edit();
                //save last deleted Task in SP
                edit.putString("deletedTaskName",task.getTaskName());
                edit.putString("deletedTaskDetails",task.getDetails());
                edit.putString("deletedTaskStatus",task.getStatus());
                edit.putInt("deletedTaskHour",task.getHour());
                edit.putInt("deletedTaskMin",task.getMin());
                edit.putInt("deletedTaskYear",task.getYear());
                edit.putInt("deletedTaskMonth",task.getMonth());
                edit.putInt("deletedTaskDay",task.getDay());
                edit.apply(); //apply saving in sp or we can do edit.commit()
            }

            //if user press done in checkbox we set in the database that task done (in database we make replace in case of conflict)
            //because of that when database found that the task with exist ID it replace it and not add new one
            @Override
            public void onApproveTaskDone(Task task) {
                viewModel.insertTask(task);
            }

            @Override
            public void onEditTaskClick(Task task) {
                listener.onClickEditTask(task);
            }
        },getContext());
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext()); //get context of activity
        recyclerView.setHasFixedSize(true);  //increase performance and set the size in size of parent
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        boolean statusSort = sp.getBoolean("Sort", false);  //use sp also for prefernces
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class); //return instance of viewmodel
        viewModel.GetAllTasks().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                    ArrayList<Task> task1 = new ArrayList<>(tasks);
                    //Toast.makeText(getContext(), "i set preference", Toast.LENGTH_SHORT).show();
                    boolean SPstatus=sp.getBoolean("SwitchSaveSort",false);
                    if(SPstatus) //if recycler refresh it read on changed because of that we check preference here also
                        Collections.sort(task1, Task.sortBy("SortByDate"));
                    else
                        Collections.sort(task1,Task.sortBy("SortByID"));

                    tasksListgettenLive.clear(); //if we didn't make clear then he add the existing items again
                    tasksListgettenLive.addAll(task1); //save the updated instance to use for search (after we close search we need to display the list)
                   // task1.addAll(tasks);
                    adapter.setTasks(task1);
                }

        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //its not nisscary put infalate here we can also put in onCreateView
        addnewTaskButt=view.findViewById(R.id.AddnewTaskBut);  //inflate the fragment_recycler_view_tasks_list
        //in this button we go to the add new task fragment
        addnewTaskButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickAddNewTask();
            }
        });

    //    sp= PreferenceManager.getDefaultSharedPreferences(getContext());
    //    edit = sp.edit();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s!=null&&s.equals("SwitchSaveSort")) //we check if s null because we get null pointer exception for string
        {
            Toast.makeText(getContext(), "i set preference", Toast.LENGTH_SHORT).show();
            List<Task> task2=new ArrayList<>(tasksListgettenLive);
            boolean SPstatus=sharedPreferences.getBoolean("SwitchSaveSort",false);
            if(SPstatus) //sort by date
            {
                Collections.sort(task2, Task.sortBy("SortByDate"));
                adapter.setTasks((ArrayList)task2);
                adapter.notifyDataSetChanged();
                // Toast.makeText(getContext(), task1.get(0).getTaskName(), Toast.LENGTH_SHORT).show();
            }
            else  //in the normal, the tasks displayed in recyclerview by id (sorted by id) (then if we make sort by id then we get the tasks in the normal type)
            {
                Collections.sort(task2, Task.sortBy("SortByID"));
                adapter.setTasks((ArrayList)task2);
                adapter.notifyDataSetChanged();
            }
        }
    }


    public interface onFragmentClickListener{
        void onFragmentShowInteraction(Task task);  //to cominucate with the fragment of display data of item (we send task to display it in the other fragment)
        void onClickAddNewTask();
        void onClickEditTask(Task task); //user press editButton then we send task to activity then activity send to the edit fragment and display it

    }
}