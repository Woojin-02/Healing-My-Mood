package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteDiaryActivity extends AppCompatActivity {

    private String diaryResponse = "";
    private String diaryDate = "";
    private String diaryTitle = "";
    private String diaryEmotion = "";
    private String diaryDetail = "";

    private EditText eventEditText;
    private EditText thinkEditText;
    private EditText emotionEditText;
    private EditText actionEditText;
    private EditText resultEditText;

    private ImageView ivGoBackMenu;
    private ImageView ivCrateDiary;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private static final String MY_SECRET_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writediary);

        ivGoBackMenu = findViewById(R.id.iv_goBack_writeToMain);
        ivCrateDiary = findViewById(R.id.iv_create_diary);

        eventEditText = findViewById(R.id.add_event);
        thinkEditText = findViewById(R.id.add_think);
        emotionEditText = findViewById(R.id.add_emotion);
        actionEditText = findViewById(R.id.add_action);
        resultEditText = findViewById(R.id.add_result);

        ivGoBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                // intent 종료
                finish();
            }
        });

        ivCrateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 일기 생성 클릭됨");

                // EditText에서 텍스트 가져오기
                String eventText = eventEditText.getText().toString();
                String thinkText = thinkEditText.getText().toString();
                String emotionText = emotionEditText.getText().toString();
                String actionText = actionEditText.getText().toString();
                String resultText = resultEditText.getText().toString();

                // userApiRequests에 내용 합치기
                String userApiRequests = "사건: " + eventText +
                        "\n생각: " + thinkText +
                        "\n감정: " + emotionText +
                        "\n행동: " + actionText +
                        "\n결과: " + resultText;



                // 일기 생성 메세지 전송
                callDiaryAPI(userApiRequests);
                Toast.makeText(getApplicationContext(), "일기가 생성중입니다", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "잠시만 기다려주세요", Toast.LENGTH_LONG).show();
            }
        });

        //연결시간 설정. 60초/120초/60초
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    void callDiaryAPI(String userApiRequests) {
        // 일기 작성 요청을 보낼 prompt
        String diaryPrompt = "위 내용으로 일기 만들어줘. 양식은 아래와 같이 해줘.\n" +
                "'제목: ',\n" +
                "'감정: '(명사로 작성)\n" +
                "'내용: '";

        // 추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMessage = new JSONObject();
        try {
            baseAi.put("role", "system");
            baseAi.put("content", diaryPrompt);
            // 유저 메세지
            userMessage.put("role", "user");
            userMessage.put("content", userApiRequests);
            // array로 담아서 한번에 보낸다
            arr.put(baseAi);
            arr.put(userMessage);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            // 모델명 변경
            object.put("model", "ft:gpt-3.5-turbo-0613:personal::8HU2luS1");
            object.put("messages", arr);
            object.put("max_tokens", 1024);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "네트워크 요청 실패", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        diaryResponse = result.trim();

                        // 결과를 반환하는 메서드를 호출
                        processDiaryResponse(diaryResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "에러");
                }
            }
        });
    }

    void processDiaryResponse(String response) {
        // 정규 표현식을 사용하여 일기 정보 추출
        Pattern datePattern = Pattern.compile("날짜: (.*?)\\n");
        Pattern titlePattern = Pattern.compile("제목: (.*?)\\n");
        Pattern emotionPattern = Pattern.compile("감정: (.*?)\\n");
        Pattern detailPattern1 = Pattern.compile("내용:(.*?)$", Pattern.DOTALL);

        Matcher dateMatcher = datePattern.matcher(response);
        Matcher titleMatcher = titlePattern.matcher(response);
        Matcher emotionMatcher = emotionPattern.matcher(response);
        Matcher detailMatcher = detailPattern1.matcher(response);

        if (dateMatcher.find()) {
            diaryDate = dateMatcher.group(1);
        }

        if (titleMatcher.find()) {
            diaryTitle = titleMatcher.group(1);
        }

        if (emotionMatcher.find()) {
            diaryEmotion = emotionMatcher.group(1);
        }

        if (detailMatcher.find()) {
            diaryDetail = detailMatcher.group(1);
        }

        // EditDiaryActivity로 데이터 전달
        Intent intent = new Intent(WriteDiaryActivity.this, EditDiaryActivity.class);

        intent.putExtra("diaryDate", diaryDate);
        intent.putExtra("diaryTitle", diaryTitle);
        intent.putExtra("diaryEmotion", diaryEmotion);
        intent.putExtra("diaryDetail", diaryDetail);

        startActivity(intent);
    }
}
