package com.example.androidfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//we make extend from the adapter of recyclerView(this adapter genric and we set for it the type of holder we want to use for make pointer for our items)
public class TaskRVadapter extends RecyclerView.Adapter<TaskRVadapter.TaskViewHolder> {

    ArrayList<Task> tasklist;
    OnItemClickListener listener;
    private static MainViewModel viewModel;
    AlertDialog dialog;
    Context context;

    public TaskRVadapter(ArrayList<Task> task,OnItemClickListener listener,Context context) {
        this.tasklist = task;
        this.listener=listener;
        this.context=context;

    }

    //this method we use it to set tasks arraylist (update it by some other classes and livedata)
    public void setTasks( ArrayList<Task> task)
    {
       this.tasklist =task;
       notifyDataSetChanged();
    }

    //onCreateViewHolder work times for all the screen (after it make items for number of items that screen can include,the layoutManger dont call it more)
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_custom_item,null,false); //here we inflate each item
       TaskViewHolder viewHolder=new TaskViewHolder(v); //make a holder
       return viewHolder;
    }

    //after each onCreateViewHolder this method called and it used for set the data to the item in the screen.
    //the holder in prototype is the one we make in onCreateViewHolder(return of onCreateViewHolder is come here)
    //for each item we display in the recyclerview the layoutmanger use this method. it override the old data and set new data.
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        Task tas= tasklist.get(position); //get the task in position i from arraylist
        holder.tv_taskname.setText(tas.getTaskName());
       // holder.tv_details.setText(tas.getDetails());
        holder.tv_date.setText(tas.getDay()+"/"+tas.getMonth()+"/"+tas.getYear());
        if(tas.getMin()>9)
            holder.tv_time.setText(tas.getHour()+":"+tas.getMin());
        else
            holder.tv_time.setText(tas.getHour()+":0"+tas.getMin());
        holder.bind(tas); //send task to inner class down (holder) to use( we send position of pressed item to the viewholder class)

        //check if task done or not. if yes it make v for the checkbox and set the relevant image
        if(tas.getStatus().equals("done"))
        {
            holder.donetaskcheckbox.setChecked(true);
            holder.imageview.setImageResource(R.drawable.baseline_task_alt_24); //replace image of item for done image
        }
        else
        {
            holder.donetaskcheckbox.setChecked(false);
            holder.imageview.setImageResource(R.drawable.baseline_watch_later_24);
        }


    }

    @Override
    public int getItemCount() {
        return tasklist.size();
    }

    //this is our holder used for point for items of the recyclerview. we make it inside our adapter because we need to use it just here
    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView tv_taskname,tv_details,tv_date,tv_time;
        ImageView imageview;
        CheckBox donetaskcheckbox;
        Task task;
        Button editTaskbut;
         public TaskViewHolder(@NonNull View itemView) { //we called this constructor from onCreateViewHolder method and give it the view witch is our item xml file
            super(itemView);
            tv_taskname=itemView.findViewById(R.id.TaskNameTV);
            tv_details=itemView.findViewById(R.id.TaskDetailsTV); //inflate for items
            tv_date=itemView.findViewById(R.id.TaskDateTV);
            tv_time=itemView.findViewById(R.id.TaskTimeTV);
            imageview=itemView.findViewById(R.id.imageView2);
            donetaskcheckbox=itemView.findViewById(R.id.checkBoxapprove);
            editTaskbut=itemView.findViewById(R.id.EditButton);

            itemView.setOnClickListener(new View.OnClickListener() { //make listener for press on item in the recyclerview
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(task);  //we send from the adapter the task object to the fragment to send it to the activity and then from the activity to the another fragment

                }
            });

            donetaskcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        task.setStatus("done"); //if checkbox pressed we change status of relevant task
                        listener.onApproveTaskDone(task);
                        editTaskbut.setEnabled(false);  //if task approve we cant edit it anymore then we eisplay edit button
                    }
                    else  //if check box in item didn't pressed then we setststus yey and edit task in the database
                    {
                        task.setStatus("yet");
                        listener.onApproveTaskDone(task);
                        editTaskbut.setEnabled(true);

                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){


                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Task")
                            .setMessage("Are you sure you want to delete this Item?(all preference will be reset)")
                            .setIcon(R.drawable.baseline_delete_forever_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (pos != RecyclerView.NO_POSITION) {
                                        Task task= tasklist.remove(pos); //remove from the task here
                                        listener.onItemLongClick(task);  //remove the task from database

                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.cancel();
                                }
                            })
                            .show(); //display dialog
                    return false;
                }
            });

            editTaskbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Task task= tasklist.get(pos); //remove from the task here
                        listener.onEditTaskClick(task);
                    }

                }
            });
        }
        //we use this method to get task from the adapter itself
        //instead of this we also can get use getadapterposition
        void bind(Task task)
        {
            this.task=task;
        }
    }
}
