package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShowDiaryActivity extends AppCompatActivity {

    TextView dateTextView, titleText, emotionText, detailText;
    int diaryCode;
    private ImageView ivGoBackShowToMain;
    private ImageView ivMoreUpdateAndDelete;
    private static final int MENU_ITEM_UPDATE = 1;
    private static final int MENU_ITEM_DELETE = 2;

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



        ivGoBackShowToMain=findViewById(R.id.iv_goBack_showToMain);
        ivMoreUpdateAndDelete=findViewById(R.id.iv_more_updateAndDelete);

        ivGoBackShowToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                // intent 종료
                finish();
            }
        });

        ivMoreUpdateAndDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ShowDiaryActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_show, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int it = 0;
                if(item.getItemId() == R.id.menu_item_update) {
                    it = 1;
                } else if (item.getItemId() == R.id.menu_item_delete) {
                    it = 2;
                }
                switch (it) {
                    case 1:
                        // 수정 메뉴 아이템 클릭 시 수행할 동작
//                        Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                        return true;
                    case 2:
                        // 삭제 메뉴 아이템 클릭 시 수행할 동작
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

}
