package com.example.myhealing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myhealing.adapter.DiaryAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DiaryAdapter adapter;

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

        //사용자 조회
        loadUserList();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("AI 감정일기");

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

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK){

                        //사용자 조회
                        loadUserList();
                    }
                }
            }
    );

    //사용자 조회
    private void loadUserList() {

        AppDatabase db = AppDatabase.getDBInstance(this.getApplicationContext());

        List<EmotionalDiary> userList = db.diaryDao().getAllDiary();

        //리스트 저장
        adapter.setUserList(userList);
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