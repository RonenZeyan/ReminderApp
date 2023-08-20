package com.example.androidfinalproject.databasePack;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.androidfinalproject.Task;

@Database(entities = {Task.class},version = 4)
    public  abstract class database extends RoomDatabase {

        public abstract databaseDao databasedao();
        private static database instance;
            //make just one instance for database to not make more than one database (singlton)
        public static database getInstance(Context context) {
            if(instance==null){
                instance = Room.databaseBuilder(context.getApplicationContext(),database.class,"tasks-database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return instance;
        }



    };


