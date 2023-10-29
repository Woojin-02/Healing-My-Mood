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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DiaryAdapter adapter;
    private int currentYear;
    private int currentMonth;
    private TextView yearMonthTextView;
    private TextView prevMonthButton;
    private TextView nextMonthButton;
    private ImageView ivSearch;
    private ImageView ivSortDiaryList;
    int it_flag;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_SORTING_OPTION = "sorting_option";

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

        // SharedPreferences에서 저장된 정렬 설정을 불러옴
        it_flag = loadSortingOption();

        // Adapter에 저장된 정렬 옵션을 설정
        adapter.setSortingOption(it_flag);


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
        loadDiaryListInMonth(currentYear, currentMonth);

        ivSortDiaryList=findViewById(R.id.iv_sort_diary_list);
        ivSortDiaryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMainMenu(v);
            }
        });

        ivSearch=findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일기 검색 Activity 실행
                Intent Searchintent = new Intent(MainActivity.this, SearchDiaryActivity.class);
                startActivity(Searchintent);
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
                loadDiaryListInMonth(currentYear, currentMonth);
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
                loadDiaryListInMonth(currentYear, currentMonth);
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
                        loadDiaryListInMonth(currentYear, currentMonth);
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
                if (it == 0) {
                    it_flag = 0;
                } else if (it == 1) {
                    it_flag = 1;
                } else if (it == 2) {
                    it_flag = 2;
                } else if (it == 3) {
                    it_flag = 3;
                }
                // SharedPreferences에 정렬 설정 저장
                saveSortingOption(it_flag);

                adapter.setSortingOption(it_flag);
                loadDiaryListInMonth(currentYear, currentMonth);
                return true;
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
                        loadDiaryListInMonth(currentYear, currentMonth);
                    }
                }
            }
    );

    //사용자 조회
    private void loadDiaryListInMonth(int currentYear, int currentMonth) {
        // 년도와 월을 가져와서 yyyy-MM 형태의 문자열로 만듭니다
        String yearMonthPrefix = String.format("%04d-%02d-", currentYear, currentMonth);
        AppDatabase db = AppDatabase.getDBInstance(this.getApplicationContext());
        List<EmotionalDiary> DiaryList = db.diaryDao().getDiaryByMonth(yearMonthPrefix);
//        Toast.makeText(this, String.valueOf(flag), Toast.LENGTH_SHORT).show();
        //리스트 저장
        adapter.setDiaryList(DiaryList);
    }

    private void saveSortingOption(int option) {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PREF_SORTING_OPTION, option);
        editor.apply();
    }

    private int loadSortingOption() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(PREF_SORTING_OPTION, DiaryAdapter.SORT_RECENT);
    }
}