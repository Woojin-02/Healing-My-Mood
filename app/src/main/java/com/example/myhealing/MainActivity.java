package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhealing.adapter.DiaryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private ImageView ivSortDiaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView 초기화 및 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //DiaryAdapter 초기화
        adapter = new DiaryAdapter(MainActivity.this);

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

        ivSortDiaryList=findViewById(R.id.iv_sort_diary_list);
        ivSortDiaryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMainMenu(v);
            }
        });

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
                        loadUserListInMonth(currentYear, currentMonth);
                    }
                });
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼이 클릭되었을 때 수행할 동작
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showPopupMainMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int it = 0;
                if(item.getItemId() == R.id.menu_list_most_recent) {
                    it = 0;
                } else if (item.getItemId() == R.id.menu_list_old) {
                    it = 1;
                } else if (item.getItemId() == R.id.menu_list_day_ascending) {
                    it = 2;
                } else if(item.getItemId() == R.id.menu_list_day_descending) {
                    it = 3;
                }
                // 팝업 메뉴에서 선택한 메뉴 항목에 따라 동작을 정의
                switch (it) {
                    case 0:
                        // 최근순 정렬 메뉴 항목 선택 시의 동작
                        loadDiaryListInSort(currentMonth, currentYear, 0);
                        return true;
                    case 1:
                        // 오래된순 정렬 메뉴 항목 선택 시의 동작
                        loadDiaryListInSort(currentMonth, currentYear, 1);
                        return true;
                    case 2:
                        // 날짜 오름차순 정렬 메뉴 항목 선택 시의 동작
                        loadDiaryListInSort(currentMonth, currentYear, 2);
                        return true;
                    case 3:
                        // 날짜 내림차순 정렬 메뉴 항목 선택 시의 동작
                        loadDiaryListInSort(currentMonth, currentYear, 3);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();

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
        Toast.makeText(this, yearMonthPrefix, Toast.LENGTH_SHORT).show();
        //리스트 저장
        adapter.setDiaryList(DiaryList, 3);
    }

    // 다이어리 정렬
    private void loadDiaryListInSort(int currentYear, int currentMonth, int flag) {
        // 년도와 월을 가져와서 yyyy-MM 형태의 문자열로 만듭니다
        String yearMonthPrefix = String.format("%04d-%02d-", currentYear, currentMonth);
        AppDatabase db = AppDatabase.getDBInstance(this.getApplicationContext());
        List<EmotionalDiary> DiaryList = db.diaryDao().getDiaryByMonth(yearMonthPrefix);
//        Toast.makeText(this, yearMonthPrefix, Toast.LENGTH_SHORT).show();
        //리스트 저장
        adapter.setDiaryList(DiaryList, flag);
    }


}