package com.example.androidfinalproject;

public interface OnItemClickListener {

    void OnItemClick(Task task);
    void onItemLongClick(Task task);
    void onApproveTaskDone(Task task);

    void onEditTaskClick(Task task);
}
