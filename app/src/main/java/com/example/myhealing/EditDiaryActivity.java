package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditDiaryActivity extends AppCompatActivity {

    String diaryDate;
    String diaryTitle;
    String diaryEmotion;
    String diaryDetail;
    TextView dateText;
    private ImageView ivGoBackMenu;
    private TextView ivCreateDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // TextView 찾기
        dateText = findViewById(R.id.date_text);
        // 현재 날짜 가져오기
        Date currentDate = Calendar.getInstance().getTime();
        // 날짜 형식 지정 (yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // 날짜 설정
        dateText.setText(dateFormat.format(currentDate));

        // Intent로부터 데이터 추출
        Intent intent = getIntent();
        diaryDate = intent.getStringExtra("diaryDate");
        diaryTitle = intent.getStringExtra("diaryTitle");
        diaryEmotion = intent.getStringExtra("diaryEmotion");
        diaryDetail = intent.getStringExtra("diaryDetail");

        // EditText 위젯을 찾아옵니다
        EditText editTitle = findViewById(R.id.edit_title);
        // diaryDetail 값을 EditText에 설정
        editTitle.setText(diaryTitle);

        // EditText 위젯을 찾아옵니다
        EditText editEmotion = findViewById(R.id.edit_emotion);
        // diaryDetail 값을 EditText에 설정
        editEmotion.setText(diaryEmotion);

        // EditText 위젯을 찾아옵니다
        EditText editDetail = findViewById(R.id.edit_detail);
        // diaryDetail 값을 EditText에 설정
        editDetail.setText(diaryDetail);

        //toolbar 버튼 설정
        ivGoBackMenu=findViewById(R.id.iv_goBack_editToBefore);
        ivGoBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                Toast.makeText(getApplicationContext(), "버튼 클릭", Toast.LENGTH_SHORT).show();
                // intent 종료
                finish();
            }
        });

        ivCreateDiary=findViewById(R.id.iv_create_diary);
        ivCreateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 생성/수정 버튼 클릭됨");

                String sTitle = editTitle.getText().toString();
                String sEmotion = editEmotion.getText().toString();
                String sDetail = editDetail.getText().toString();
                String sDate = dateText.getText().toString();

                insertDiary(sTitle, sEmotion, sDetail, sDate);
            }
        });
    }

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                    // Date Picker에서 선택한 날짜를 TextView에 설정
                    TextView tv = findViewById(R.id.date_text);
                    tv.setText(String.format("%d-%d-%d", yy,mm+1,dd));
                }
            };

    public void mOnClick_DatePick(View view) {
        // DATE Picker를 스피너 모드로 띄우기
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
    }

    private void insertDiary(String title, String emotion, String detail, String date){

        try {
            // 현재 시간 정보 가져오기
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더합니다.
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            // diaryCode 생성 (년도 뒤 4자리, 월일 4자리, 시분 4자리)
            int diaryCode = year * 100000000 + month * 1000000 + day * 10000 + hour * 100 + minute;

            // AppDatabase 인스턴스 생성
            AppDatabase db = AppDatabase.getDBInstance(getApplicationContext());

            db.runInTransaction(() -> {
                // EmotionalDiary 객체 생성 및 데이터 설정
                EmotionalDiary diary = new EmotionalDiary();
                diary.diaryCode = diaryCode;
                diary.title = title;
                diary.emotion = emotion;
                diary.detail = detail;
                diary.creationDate = date;

                // 데이터베이스에 일기 삽입
                db.diaryDao().insertDiary(diary);
            });

            Toast.makeText(getApplicationContext(), "일기 저장 성공", Toast.LENGTH_SHORT).show();

            // Intent를 사용하여 MainActivity를 시작하고 현재 활동을 종료합니다
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // 현재 활동 종료

            finish(); // 현재 액티비티 종료
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "일기 저장 중 오류가 발생했습니다: " + e.getMessage();
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
