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

    @Query("SELECT * FROM emotionalDiary WHERE creationDate LIKE :yearMonthPrefix || '%'")
    List<EmotionalDiary> getDiaryByMonth(String yearMonthPrefix);

    @Query("SELECT * FROM emotionalDiary WHERE diaryCode = :diaryCode")
    EmotionalDiary getOneDiaryByCode(int diaryCode);

    @Insert
    void insertDiary(EmotionalDiary healing);

    @Delete
    void deleteDiary(EmotionalDiary healing);

    @Query("DELETE FROM emotionalDiary WHERE diaryCode = :diaryCode")
    void deleteDiaryByCode(int diaryCode);

    @Update
    void updateDiary(EmotionalDiary healing);

}
