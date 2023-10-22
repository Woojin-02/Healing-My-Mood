package com.example.myhealing;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmotionalDiary {

    @PrimaryKey(autoGenerate = true)
    public int diaryCode;

    @ColumnInfo(name = "creationDate")
    public String creationDate;

    @ColumnInfo(name = "emotion")
    public String emotion;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "detail")
    public String detail;

}
