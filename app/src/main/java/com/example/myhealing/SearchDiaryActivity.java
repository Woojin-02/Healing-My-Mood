package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhealing.adapter.DiaryAdapter;

import java.util.List;

public class SearchDiaryActivity extends AppCompatActivity {

    private ImageView ivGoBackSearchToMain;
    private EditText etSearch;
    private ImageView ivSearch;
    private DiaryAdapter adapter;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ivSearch=findViewById(R.id.iv_et_search);
        etSearch = findViewById(R.id.et_search);

        adapter = new DiaryAdapter(this);

        // RecyclerView에 adapter 설정
        RecyclerView recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검색 버튼 클릭 시 검색어 가져오기
                searchQuery = etSearch.getText().toString();
                SearchDiaryList(searchQuery);
            }
        });

        ivGoBackSearchToMain=findViewById(R.id.iv_goBack_searchToMain);
        ivGoBackSearchToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                // Intent를 사용하여 MainActivity를 시작하고 현재 활동을 종료합니다
                Intent intent = new Intent(SearchDiaryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 이 곳에서 검색 결과를 업데이트합니다.
        // 검색어를 가져옵니다.
        searchQuery = etSearch.getText().toString();
        SearchDiaryListStart(searchQuery);
    }


    private void SearchDiaryList(String query) {
        try {
            AppDatabase db = AppDatabase.getDBInstance(getApplicationContext());
            List<EmotionalDiary> DiaryList = db.diaryDao().getDiarySearchText(query);
            adapter.setDiaryList(DiaryList);
            Toast.makeText(this, "검색이 완료되었습니다", Toast.LENGTH_LONG).show();

            // 키보드를 비활성화
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SearchDiaryActivity", "Error in SearchDiaryList: " + e.getMessage());
            // 예외 처리 또는 사용자에게 오류 메시지 표시
            Toast.makeText(this, "검색 오류", Toast.LENGTH_LONG).show();
        }
    }

    private void SearchDiaryListStart(String query) {
        try {
            AppDatabase db = AppDatabase.getDBInstance(getApplicationContext());
            List<EmotionalDiary> DiaryList = db.diaryDao().getDiarySearchText(query);
            adapter.setDiaryList(DiaryList);

            // 키보드를 비활성화
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SearchDiaryActivity", "Error in SearchDiaryList: " + e.getMessage());
            // 예외 처리 또는 사용자에게 오류 메시지 표시
            Toast.makeText(this, "검색 오류", Toast.LENGTH_LONG).show();
        }
    }
}
