package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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

import java.util.Calendar;

public class UpdateDiaryActivity extends AppCompatActivity {

    TextView updateDate;
    EditText updateTitle, updateEmotion, updateDetail;
    int diaryCode;
    private int currentYear, currentMonth, currentDay;
    private ImageView ivGoBackMenu;
    private TextView ivCreateDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // ShowDiaryActivity에서 보낸 데이터를 가져오기
        String date = getIntent().getStringExtra("creationDate");
        String title = getIntent().getStringExtra("title");
        String emotion = getIntent().getStringExtra("emotion");
        String detail = getIntent().getStringExtra("detail");
        diaryCode = getIntent().getIntExtra("diaryCode", 0);

        updateDate = findViewById(R.id.date_text);
        updateDate.setText(date);

        // EditText 위젯을 찾아옵니다
        updateTitle = findViewById(R.id.edit_title);
        // diaryDetail 값을 EditText에 설정
        updateTitle.setText(title);

        // EditText 위젯을 찾아옵니다
        updateEmotion = findViewById(R.id.edit_emotion);
        // diaryDetail 값을 EditText에 설정
        updateEmotion.setText(emotion);

        // EditText 위젯을 찾아옵니다
        updateDetail = findViewById(R.id.edit_detail);
        // diaryDetail 값을 EditText에 설정
        updateDetail.setText(detail);

        updateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatePickerDialog pd = new MyDatePickerDialog();

                String update_date = updateDate.getText().toString();
                pd.setUpdateDate(update_date);
                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 선택한 년도와 월을 yearMonthTextView에 설정
                        currentYear = year;
                        currentMonth = monthOfYear;
                        currentDay = dayOfMonth;
                        updateYearMonthText();
                    }
                });
                pd.show(getSupportFragmentManager(), "DatePickerTest");
            }
        });


        //toolbar 버튼 설정
        ivGoBackMenu=findViewById(R.id.iv_goBack_editToBefore);
        ivGoBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                // intent 종료
                finish();
            }
        });

        ivCreateDiary=findViewById(R.id.iv_create_diary);
        ivCreateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 생성/수정 버튼 클릭됨");

                String sTitle = updateTitle.getText().toString();
                String sEmotion = updateEmotion.getText().toString();
                String sDetail = updateDetail.getText().toString();
                String sDate = updateDate.getText().toString();

                updateDiary(diaryCode, sTitle, sEmotion, sDetail, sDate);
            }
        });
    }

    // updateDate의 텍스트를 업데이트합니다.
    private void updateYearMonthText() {
        String yearMonthDay = currentYear + "-" + currentMonth + "-" + currentDay;
        updateDate.setText(yearMonthDay);
    }

    private void updateDiary(int diaryCode, String title, String emotion, String detail, String date) {
        // 이 함수에서는 diaryCode를 사용하여 DB에서 해당 일기를 업데이트하는 작업을 수행합니다.
        // 업데이트가 완료되면 사용자에게 메시지를 보여줄 수 있습니다.

        // Room 데이터베이스 인스턴스를 가져옵니다.
        AppDatabase db = AppDatabase.getDBInstance(getApplicationContext());

        // diaryCode를 사용하여 기존의 일기를 찾아옵니다.
        EmotionalDiary existingDiary = db.diaryDao().getOneDiaryByCode(diaryCode);

        if (existingDiary != null) {
            // 해당 일기를 업데이트합니다.
            existingDiary.title = title;
            existingDiary.emotion = emotion;
            existingDiary.detail = detail;
            existingDiary.creationDate = date;

            // 업데이트된 일기를 저장합니다.
            db.diaryDao().updateDiary(existingDiary);

            Toast.makeText(getApplicationContext(), "일기가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("creationDate", date);
            intent.putExtra("title", title);
            intent.putExtra("emotion", emotion);
            intent.putExtra("detail", detail);
            intent.putExtra("diaryCode", diaryCode);
            setResult(RESULT_OK, intent);
            finish(); // 현재 활동 종료
        } else {
            Toast.makeText(getApplicationContext(), "일기를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
