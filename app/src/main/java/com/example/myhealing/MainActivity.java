package com.example.myhealing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhealing.adapter.DiaryAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DiaryAdapter adapter;
    private int currentYear;
    private int currentMonth;
    private TextView yearMonthTextView;
    private TextView prevMonthButton;
    private TextView nextMonthButton;


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView 초기화 및 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //DiaryAdapter 초기화
        adapter = new DiaryAdapter();

        //RecyclerView Adapter 설정
        recyclerView.setAdapter(adapter);

        yearMonthTextView = findViewById(R.id.yearMonthTextView);
        prevMonthButton = findViewById(R.id.prevMonthButton);
        nextMonthButton = findViewById(R.id.nextMonthButton);

        // 현재 날짜를 가져와서 currentYear와 currentMonth에 할당합니다.
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 +1을 합니다.

        // 초기 월을 설정합니다.
        updateYearMonthText();
        //사용자 조회
        loadUserListInMonth(currentYear, currentMonth);

        prevMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 월을 감소합니다.
                currentMonth--;

                // 현재 월이 1월인 경우 현재 연도를 감소합니다.
                if (currentMonth == 0) {
                    currentMonth = 12;
                    currentYear--;
                }
                updateYearMonthText();
                loadUserListInMonth(currentYear, currentMonth);
            }
        });

        // nextMonthButton 클릭 이벤트를 처리합니다.
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMonth == 12) {
                    currentYear++;
                    currentMonth = 1;
                } else {
                    currentMonth++;
                }
                updateYearMonthText();
                loadUserListInMonth(currentYear, currentMonth);
            }
        });

        yearMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 선택한 년도와 월을 yearMonthTextView에 설정
                        currentYear = year;
                        currentMonth = monthOfYear;
                        updateYearMonthText();
                    }
                });
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
                loadUserListInMonth(currentYear, currentMonth);
            }
        });

        // 버튼을 XML에서 찾아옴
        Button buttonBottomLeft = findViewById(R.id.btn_bottom_left);

        // 버튼 클릭 이벤트 처리
        buttonBottomLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭되었을 때 수행할 동작
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    // yearMonthTextView의 텍스트를 업데이트합니다.
    private void updateYearMonthText() {
        String yearMonth = currentYear + "년 " + currentMonth + "월";
        yearMonthTextView.setText(yearMonth);
    }

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK){

                        //사용자 조회
                        loadUserListInMonth(currentYear, currentMonth);
                    }
                }
            }
    );

    //사용자 조회
    private void loadUserListInMonth(int currentYear, int currentMonth) {
        // 년도와 월을 가져와서 yyyy-MM 형태의 문자열로 만듭니다
        String yearMonthPrefix = String.format("%04d-%02d-", currentYear, currentMonth);
        AppDatabase db = AppDatabase.getDBInstance(this.getApplicationContext());
        List<EmotionalDiary> DiaryList = db.diaryDao().getDiaryByMonth(yearMonthPrefix);
//        Toast.makeText(this, yearMonthPrefix, Toast.LENGTH_SHORT).show();
        //리스트 저장
        adapter.setDiaryList(DiaryList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return false;
    }

    public void database_data(View view) {
        AppDatabase db = AppDatabase.getDBInstance(this.getApplicationContext());
        // 데이터베이스가 열려 있는지 확인
        if (db.isOpen()) {
            Toast.makeText(this, "데이터베이스가 열려 있습니다.", Toast.LENGTH_SHORT).show();

            // 테이블이 생성되어 있는지 확인
            if (!db.inTransaction()) {
                Toast.makeText(this, "테이블이 생성되어 있습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "테이블이 생성되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            }

            // 데이터베이스의 데이터 확인
            List<EmotionalDiary> diaryList = db.diaryDao().getAllDiary();

            if (diaryList != null && !diaryList.isEmpty()) {
                // 데이터베이스에 데이터가 존재함
                int numberOfDiaries = diaryList.size();
                Toast.makeText(this, "데이터베이스에 " + numberOfDiaries + " 개의 일기가 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 데이터베이스가 비어있음
                Toast.makeText(this, "데이터베이스에 저장된 일기가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "데이터베이스가 열려 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}