package com.example.myhealing;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowDiaryActivity extends AppCompatActivity {

    TextView dateTextView, titleText, emotionText, detailText;
    int diaryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdiary);

        //초기화
        dateTextView = findViewById(R.id.date_text_view);
        titleText = findViewById(R.id.title_text);
        emotionText = findViewById(R.id.emotion_text);
        detailText = findViewById(R.id.detail_text);

        //아이템 어뎁터에서 넘어온 데이터 변수에 담기
        String date = getIntent().getStringExtra("creationDate");
        String title = getIntent().getStringExtra("title");
        String emotion = getIntent().getStringExtra("emotion");
        String detail = getIntent().getStringExtra("detail");
        diaryCode = getIntent().getIntExtra("diaryCode", 0);

        //변수 화면에 보여주기
        dateTextView.setText(date);
        titleText.setText(title);
        emotionText.setText(emotion);
        detailText.setText(detail);

    }

}
