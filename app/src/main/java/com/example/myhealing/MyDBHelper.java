package com.example.myhealing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    // 나중에 데이터베이스를 변경하려면 버전을 증가시키면 됩니다.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "myHealingMood.db";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        db.execSQL("CREATE TABLE EmotionalDiary (diaryCode INTEGER PRIMARY KEY, creationDate TEXT, emotion TEXT, title TEXT, detail TEXT)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전이 증가하면 해당 테이블을 삭제하고 다시 생성합니다.
        db.execSQL("DROP TABLE IF EXISTS EmotionalDiary");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

