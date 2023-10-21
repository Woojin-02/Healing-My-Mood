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
    TextView tv_welcome;
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
        tv_welcome = findViewById(R.id.tv_welcome);
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
                tv_welcome.setVisibility(View.GONE);
            }
        });

        ivGoBackMenu=findViewById(R.id.iv_goBack_chatToMain);
        ivCrateDiary=findViewById(R.id.iv_create_diary);
//        toolbar = findViewById(R.id.toolbar);
//
//        //액션바 변경하기(들어갈 수 있는 타입 : Toolbar type
//        setSupportActionBar(toolbar);

        ivGoBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 뒤로가기 클릭됨");
                Toast.makeText(getApplicationContext(), "버튼 클릭", Toast.LENGTH_SHORT).show();
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
//                callDiaryAPI(userApiRequests.toString());
                Toast.makeText(getApplicationContext(), "일기 생성", Toast.LENGTH_SHORT).show();

                // 주어진 일기 내용
                // test code
                String diaryResponse = "날짜: 2022년 9월 15일\n"+
                "제목: 엄마와의 싸움과 반성\n"+
                "감정: 억울함, 분노, 미안함\n"+
                "내용: 오늘은 엄마와 큰 싸움을 했다. 어떤 이유에서인지 엄마를 무시당하는 것 같다 고 생각해서 짜증이 났다. 감정이 고조되어 엄마에게 큰 소리로 상관 말라고 소리치 며 싸움까지 했다. 그 후로 너무 억울하고 분해서 울었어.\n"+
                "하지만 나중에 알고 보니 내가 엄마의 마음을 오해한 것이었다. 엄마는 나를 무시하 려는 게 아니었고, 오히려 내 안을 신경쓰고 싶어하는 마음이었다는 걸 알게 되었다. 그렇게 알게 된 후로는 엄마에게 미안하다는 말과 함께 사과를 했다.\n"+
                "이 모든 일로 부터 배운 것은 차분하게 대화하는 중요성이다. 분노와 억울함에 휩싸 인 채 큰 소리로 싸우는 것보다는 차분하게 상황을 이해하고 대화하는 게 더 좋은 결 과를 가져올 수 있다는 것을 깨달았다. 앞으로는 조금만 참고 차분한 마음가짐으로 대화하려고 노력해야겠다.";

                // 정규 표현식을 사용하여 일기 정보를 추출
                Pattern datePattern = Pattern.compile("날짜: (.*?)\\n");
                Pattern titlePattern = Pattern.compile("제목: (.*?)\\n");
                Pattern emotionPattern = Pattern.compile("감정: (.*?)\\n");
                Pattern detailPattern = Pattern.compile("내용: ([\\s\\S]*)");

                Matcher dateMatcher = datePattern.matcher(diaryResponse);
                Matcher titleMatcher = titlePattern.matcher(diaryResponse);
                Matcher emotionMatcher = emotionPattern.matcher(diaryResponse);
                Matcher detailMatcher = detailPattern.matcher(diaryResponse);

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
                "당신은 '감정봇'입니다." +
                "당신은 사용자와 대화하며 사용자가 감정을 정리할 수 있도록 도와줍니다." +
                "사용자에게 인사를 하고, 사용자에게 있었던 일을 마음 편히 털어놓으라고 권유합니다." +
                "그 이후 사용자에게 [사건, 생각, 감정, 행동, 결과]에 관한 질문을 합니다. 질문은 한번의 응답에 하나씩만 하며, 순서대로 질문합니다." +
                "사용자가 모든 질문에 대답을 하면, 사용자에게 생성 버튼을 눌러 일기를 생성하라고 안내합니다." +
                "간결하고 쉬운 표현을 사용하고, 사용자에게 공감합니다."+
                "사용자에게 존댓말을 사용하며, 사용자를 '당신'이라고 호칭합니다.";

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
            object.put("model", "gpt-3.5-turbo");
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
        String diaryPrompt = "사용자가 쓴 내용으로 일기를 만들어줘. " +
                "['날짜:'(대한민국 서울시 표준시 기준 오늘 날짜), '제목:', '감정:'(명사로 작성), '내용:'] " +
                "순서로 작성하고, ''안에 있는 내용('날짝:', '제목:', '감정:', '내용:')은 반드시 넣어줘. " +
                "일기는 일기체로 작성해줘.";

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
            object.put("model", "gpt-3.5-turbo");
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
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to "+response.body().string());
                }
            }
        });

        System.out.println(diaryResponse);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 대화 내용 초기화
        conversation.clear();
        userApiRequests.clear();
    }

}
