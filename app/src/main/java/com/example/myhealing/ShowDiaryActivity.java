package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShowDiaryActivity extends AppCompatActivity {

    TextView dateTextView, titleText, emotionText, detailText;
    int diaryCode;
    private ImageView ivGoBackShowToMain;
    private ImageView ivMoreUpdateAndDelete;
    private static final int YOUR_UPDATE_REQUEST_CODE = 1;

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
                // 현재 활동의 클래스명을 가져옴
                String currentActivityName = ShowDiaryActivity.this.getClass().getSimpleName();

                // 이전 액티비티에 따라 다른 액티비티를 시작
                Intent intent;
                if (currentActivityName.equals("MainActivity")) {
                    intent = new Intent(ShowDiaryActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (currentActivityName.equals("SearchDiaryActivity")) {
                    intent = new Intent(ShowDiaryActivity.this, SearchDiaryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 다른 이전 액티비티가 있는 경우 처리해야 함
                    // 기본적으로 MainActivity를 실행
                    finish();
                }
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
                        updateDiary();
                        return true;
                    case 2:
                        // 삭제 메뉴 아이템 클릭 시 수행할 동작
                        deleteDiary();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void updateDiary() {
        // 다섯 가지 데이터 가져오기
        String date = getIntent().getStringExtra("creationDate");
        String title = getIntent().getStringExtra("title");
        String emotion = getIntent().getStringExtra("emotion");
        String detail = getIntent().getStringExtra("detail");
        diaryCode = getIntent().getIntExtra("diaryCode", 0);

        // UpdateDiaryActivity로 데이터 전달을 위한 Intent 생성
        Intent intent = new Intent(ShowDiaryActivity.this, UpdateDiaryActivity.class);
        intent.putExtra("creationDate", date);
        intent.putExtra("title", title);
        intent.putExtra("emotion", emotion);
        intent.putExtra("detail", detail);
        intent.putExtra("diaryCode", diaryCode);

        // UpdateDiaryActivity 실행
        startActivityForResult(intent, YOUR_UPDATE_REQUEST_CODE);
    }

    private void deleteDiary() {
        // 사용자에게 확인 대화 상자를 표시하여 일기를 삭제할 것인지 물어봅니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말로 이 일기를 삭제하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 확인을 클릭한 경우, 일기를 삭제하는 코드를 추가합니다.
                // diaryCode를 사용하여 DB에서 일기를 삭제하는 작업을 수행합니다.
                // 삭제가 완료되면 사용자에게 메시지를 보여줄 수 있습니다.
                // 예를 들어:
                 deleteDiaryFromDatabase(diaryCode);


                // 삭제가 완료되면 사용자에게 메시지를 보여줄 수 있습니다.
                Toast.makeText(getApplicationContext(), "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                // 현재 활동의 클래스명을 가져옴
                String currentActivityName = ShowDiaryActivity.this.getClass().getSimpleName();

                // 이전 액티비티에 따라 다른 액티비티를 시작
                Intent intent;
                if (currentActivityName.equals("MainActivity")) {
                    intent = new Intent(ShowDiaryActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (currentActivityName.equals("SearchDiaryActivity")) {
                    intent = new Intent(ShowDiaryActivity.this, SearchDiaryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 다른 이전 액티비티가 있는 경우 처리해야 함
                    // 기본적으로 MainActivity를 실행
                    finish();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소를 클릭한 경우, 아무 작업을 수행하지 않고 대화 상자를 닫습니다.
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // 이 메서드를 호출하여 DB에서 일기를 삭제하도록 구현해야 합니다.
    private void deleteDiaryFromDatabase(int diaryCode) {
        // diaryCode를 사용하여 DB에서 해당 일기를 삭제하는 작업을 수행합니다.
        // 이 부분은 DB 연동 및 삭제 로직에 따라 구현되어야 합니다.
        // Room 데이터베이스 인스턴스를 가져옵니다.
        // AppDatabase 인스턴스 생성
        AppDatabase db = AppDatabase.getDBInstance(getApplicationContext());

        // 해당 diaryCode를 사용하여 일기를 삭제합니다.
        db.diaryDao().deleteDiaryByCode(diaryCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YOUR_UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            String updatedDate = data.getStringExtra("creationDate");
            String updatedTitle = data.getStringExtra("title");
            String updatedEmotion = data.getStringExtra("emotion");
            String updatedDetail = data.getStringExtra("detail");

            // 여기서 수정된 데이터를 화면에 업데이트하거나 처리합니다.
            dateTextView.setText(updatedDate);
            titleText.setText(updatedTitle);
            emotionText.setText(updatedEmotion);
            detailText.setText(updatedDetail);
        }
    }

}
