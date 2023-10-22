package com.example.myhealing;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {EmotionalDiary.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmotionalDiaryDao diaryDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDBInstance(Context context){

        //INSTANCE가 null이면 초기화
        if(INSTANCE == null){

            INSTANCE = Room.databaseBuilder(context.getApplicationContext()
                            , AppDatabase.class, "MyHealingMood")
                    .allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;
    }

}
