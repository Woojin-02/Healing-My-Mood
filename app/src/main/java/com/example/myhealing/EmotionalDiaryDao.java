package com.example.myhealing;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EmotionalDiaryDao {

    @Query("SELECT * FROM emotionalDiary")
    List<EmotionalDiary> getAllDiary();

    @Query("SELECT diaryCode FROM emotionaldiary")
    List<EmotionalDiary> getDiaryById();

    @Insert
    void insertDiary(EmotionalDiary healing);

    @Delete
    void deleteDiary(EmotionalDiary healing);

    @Update
    void updateDiary(EmotionalDiary healing);

}
