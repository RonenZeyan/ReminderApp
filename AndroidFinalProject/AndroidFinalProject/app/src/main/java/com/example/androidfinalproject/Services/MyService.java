package com.example.androidfinalproject.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidfinalproject.MainActivity;
import com.example.androidfinalproject.MainViewModel;
import com.example.androidfinalproject.R;
import com.example.androidfinalproject.Task;
import com.example.androidfinalproject.databasePack.database;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyService extends Service {

    //here we get the data directly from database because in android websites they said its not good to use service with viewmodel
    String CHANNEL_ID = "CHANNEL_SAMPLE";
    private static MainViewModel viewModel;
    database db;
    Notification.Builder NotifyBuilder;
    NotificationManager notificationManager;
    java.util.Date date;
    ZonedDateTime time;
    LocalDate lt;
    boolean flag=false;
    Intent Brodcastintent = new Intent();

    public MyService() {
    }



    @Override
    public void onCreate() {
        super.onCreate();
        db= database.getInstance(getApplicationContext());
        date= new java.util.Date();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(1,getNotificationObject("reminder")); //bind the notification with the foreground service
        new Thread(new Runnable() { //create worker thread for make the mission.
            @Override
            public void run() {

                while (true) {
                    try { //thread sleep 10 min
                        Thread.sleep(60000); //(1000 millis = 1 second)
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    //viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainViewModel.class);
                    List<Task> AllTask=new ArrayList<>();
                    //startForeground(1,getNotificationObject(java.time.LocalDate.now().getYear()+""));
                    // AllTask =(ArrayList<Task>)db.databasedao().GetAllTasks().getValue();  //its not good to use viewmodel with service then we use with database directly
                     AllTask=db.databasedao().getnearlyTask(java.time.LocalDate.now().getDayOfMonth(), java.time.LocalDate.now().getMonthValue(),java.time.LocalDate.now().getYear());
                    while(AllTask==null||AllTask.size()==0)
                    {
                        AllTask=db.databasedao().getnearlyTask(java.time.LocalDate.now().getDayOfMonth(), java.time.LocalDate.now().getMonthValue(),java.time.LocalDate.now().getYear());
                    }
                    for(Task temp : AllTask) {
                        if(temp.getStatus().equals("yet")&&checkIfTaskShouldDone(temp))
                        {
                            flag=true;
                            startForeground(1,getNotificationObject(temp.getTaskName()));
                        }
                    }
//                    if(flag)
//                    {
//                        startForeground(1,getNotificationObject(temp));
//                    }
//                    try { //thread sleep 10 min
//                        Thread.sleep(60000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            }
        }).start();

        return START_STICKY;  //in case service stop to work(no enough RAM for example), it start again
        //return super.onStartCommand(intent, flags, startId);
    }

    boolean checkIfTaskShouldDone(Task task)
    {
        //check if date of task small than date of today then we
        if(task.getYear()<java.time.LocalDate.now().getYear()) return true;
        if(task.getYear()==java.time.LocalDate.now().getYear()&&task.getMonth()<java.time.LocalDate.now().getMonthValue()) return true;
        if(task.getYear()==java.time.LocalDate.now().getYear()&&task.getMonth()==java.time.LocalDate.now().getMonthValue()&&task.getDay()<java.time.LocalDate.now().getDayOfMonth()) return true;

        if(task.getYear()==java.time.LocalDate.now().getYear())
        {
          if(task.getMonth()==java.time.LocalDate.now().getMonthValue())
          {
              if(task.getDay()==java.time.LocalDate.now().getDayOfMonth())
              {
                          if ((task.getHour())-3 == java.time.LocalTime.now().getHour())  //the diffrence between israel time and this date object is 3 hours
                          {
                              if (java.time.LocalTime.now().getMinute() >= task.getMin() + 10)
                                  return true;
                          }
              }

          }
        }
        return false;

    }

    //we create here the channel of the notification and the notifcation it self
    public Notification getNotificationObject(String taskname) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CHANNEL display Task", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("reminder user for task ");

        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(channel);

        Intent intent = new Intent(this, MainActivity.class); //make intent for set for action for user
        //using pending intent instead of intent for get security. if something happen outside our application we use pending intent
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0); //using pending intent instead of intent לטפל בaction
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.baseline_notifications_none_24).setContentTitle(taskname).setContentText("this is the app of mangement task,i remind you to make the task hurry the time passed and you didn't done yet!!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pi)
                .addAction(R.drawable.baseline_notifications_none_24,"SHOW",pi);//we set setting of the reminder we will display in case user forget to make task

        return builder.build();
    }



    //used for bound service
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}