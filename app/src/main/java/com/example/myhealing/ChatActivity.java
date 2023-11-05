package com.example.myhealing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhealing.adapter.MessageAdapter;
import com.example.myhealing.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class ChatActivity extends AppCompatActivity {

    RecyclerView recycler_view;
    EditText et_msg;
    ImageButton btn_send;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    String diaryResponse = "";
    String diaryDate = "";
    String diaryTitle = "";
    String diaryEmotion = "";
    String diaryDetail = "";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private List<JSONObject> conversation = new ArrayList<>();
    List<String> userApiRequests = new ArrayList<>();

    private ImageView ivGoBackMenu;
    private ImageView ivCrateDiary;
//    private Toolbar toolbar;

    private static final String MY_SECRET_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_space);

        recycler_view = findViewById(R.id.recycler_view);
        et_msg = findViewById(R.id.et_msg);
        btn_send = findViewById(R.id.btn_send);

        recycler_view.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recycler_view.setLayoutManager(manager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recycler_view.setAdapter(messageAdapter);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_msg.getText().toString().trim();
                addToChat(question, Message.SENT_BY_ME);
                et_msg.setText("");
                callAPI(question);
            }
        });

        ivGoBackMenu=findViewById(R.id.iv_goBack_chatToMain);
        ivCrateDiary=findViewById(R.id.iv_create_diary);

        ivGoBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                // intent 종료
                finish();
                // 대화 내용 초기화
                conversation.clear();
                userApiRequests.clear();
            }
        });

        ivCrateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 일기 생성 클릭됨");
                // 일기 생성 메세지 전송
                callDiaryAPI(userApiRequests.toString());
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

    void addToChat(String message, String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recycler_view.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response, Message.SENT_BY_BOT);
    }

    // 대화 내용 추가 메서드
    private void addMessage(String role, String content) {
        JSONObject message = new JSONObject();
        try {
            message.put("role", role);
            message.put("content", content);
            conversation.add(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    void callAPI(String question){
        String prompt =
                "당신은 상냥한 감정봇입니다. 사용자의 감정에 대한 사건, 생각, 감정, 행동, 결과에 대해 묻고 대답을 들으세요.";

        userApiRequests.add(question);

        //okhttp
        messageList.add(new Message("...", Message.SENT_BY_BOT));

        //추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject userMsg = new JSONObject();
        // 첫 번째 메시지는 초기 지침을 포함하는 prompt 역할을 합니다.
        JSONObject initialPrompt = new JSONObject();
        try {
            initialPrompt.put("role", "system");
            initialPrompt.put("content", prompt);
            conversation.add(initialPrompt);

            // 이전 대화 내용을 추가합니다.
            for (JSONObject message : conversation) {
                arr.put(message);
            }
            //유저 메세지
            userMsg.put("role", "user");
            userMsg.put("content", question);
            //array로 담아서 한번에 보낸다
            arr.put(userMsg);

            // addMessage 함수를 사용하여 사용자의 입력을 대화 내용에 추가
            addMessage("user", question);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            //모델명 변경
            object.put("model", "ft:gpt-3.5-turbo-0613:personal::8HSev4zF");
            object.put("messages", arr);
            object.put("max_tokens", 1024);
//            object.put("temperature", 1);
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer "+MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to "+response.body().string());
                }
            }
        });
    }

    void callDiaryAPI(String userApiRequests) {
        // 일기 작성 요청을 보낼 prompt
        String diaryPrompt = "위 내용으로 일기 만들어줘. 양식은 아래와 같이 해줘.\n" +
                "'제목:',\n" +
                "'감정:'(명사로 작성)\n" +
                "'내용:'";

        //okhttp
        messageList.add(new Message("...", Message.SENT_BY_BOT));

        //추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMessage = new JSONObject();
        try {
            baseAi.put("role", "system");
            baseAi.put("content", diaryPrompt);
            //유저 메세지
            userMessage.put("role", "user");
            userMessage.put("content", userApiRequests);
            //array로 담아서 한번에 보낸다
            arr.put(baseAi);
            arr.put(userMessage);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            //모델명 변경
            object.put("model", "ft:gpt-3.5-turbo-0613:personal::8HU2luS1");
            object.put("messages", arr);
            object.put("max_tokens", 256);
//            object.put("temperature", 1);
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer "+MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                        diaryResponse = result.trim();

                        // 결과를 반환하는 메서드를 호출
                        processDiaryResponse(diaryResponse);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to "+response.body().string());
                }
            }
        });

    }

    void processDiaryResponse(String response) {
        // 정규 표현식을 사용하여 일기 정보를 추출
        Pattern datePattern = Pattern.compile("날짜: (.*?)\\n");
        Pattern titlePattern = Pattern.compile("제목: (.*?)\\n");
        Pattern emotionPattern = Pattern.compile("감정: (.*?)\\n");
        Pattern detailPattern = Pattern.compile("내용: ([\\s\\S]*)");

        Matcher dateMatcher = datePattern.matcher(response);
        Matcher titleMatcher = titlePattern.matcher(response);
        Matcher emotionMatcher = emotionPattern.matcher(response);
        Matcher detailMatcher = detailPattern.matcher(response);

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

        //EditDiaryActivity로 데이터 전달
        Intent intent = new Intent(ChatActivity.this, EditDiaryActivity.class);

        intent.putExtra("diaryDate", diaryDate);
        intent.putExtra("diaryTitle", diaryTitle);
        intent.putExtra("diaryEmotion", diaryEmotion);
        intent.putExtra("diaryDetail", diaryDetail);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 대화 내용 초기화
        conversation.clear();
        userApiRequests.clear();
    }

}
