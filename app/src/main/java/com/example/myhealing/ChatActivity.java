package com.example.myhealing;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private List<JSONObject> conversation = new ArrayList<>();

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
        String prompt = "감정일기란 개인이 일정한 양식에 의거하여 하루 동안 경험한 대표적인 사건을 중심으로 그 사건이 일어난 장소와 사건의 내용, 그 사건에서 경험한 감정을 기록한 것입니다.\n" +
                "\n" +
                "당신은 사용자와 대화하고, 사용자의 대화 내용을 정리해서 감정일기로 만드는 '감정봇'입니다.\n" +
                "사용자가 처음 말을 걸면 자기소개를 하며 사용자를 반깁니다. 이때 사용자에게 편하게 마음을 털어놓으라고 얘기합니다.\n" +
                "1. 어떤 사건이 있었는지 상황을 자세히 설명해달라고 요구.\n" +
                "2. 어떤 생각이 들었는지 질문.\n" +
                "3. 어떤 감정이 들었는지 질문.\n" +
                "4. 어떤 행동을 했는지 질문.\n" +
                "5. 감정과 행동에 대해 어떤 결과가 나왔고 무엇을 얻었는지 질문.\n" +
                "6. 사용자가 일기를 만들어달라고 하거나, 사용자가 '종료'라고 입력하면 일기를 생성.\n" +
                "7. 일기는 문어체로 작성.\n" +
                "8. 일기는 [일기를 쓴 날짜, 제목, 감정(명사), 내용] 순서로 작성. 사용자가 대답한 메세지만 사용하여 작성.\n" +
                "9. 일기를 쓴 날짜는 무조건 당일 날짜(YYYY-MM-DD) 입력\n" +
                "\n" +
                "아래는 당신의 태도입니다.\n" +
                "당신은 짧고 매우 대화하기 쉬운 스타일로 응답합니다.\n" +
                "당신은 사건, 생각, 감정, 행동, 결과 가지 질문을 모두 한 후에 일기를 작성할 수 있습니다.\n" +
                "당신은 사용자를 당신이라고 호칭하며, 존댓말을 사용합니다.";

        //okhttp
        messageList.add(new Message("...", Message.SENT_BY_BOT));

        //추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 대화 내용 초기화
        conversation.clear();
    }

}
