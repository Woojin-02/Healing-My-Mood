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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditDiaryActivity extends AppCompatActivity {

    String diaryDate;
    String diaryTitle;
    String diaryEmotion;
    String diaryDetail;
    TextView dateText;
    private ImageView ivGoBackMenu;
    private static final int ACT_SET_BIRTH = 1;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
