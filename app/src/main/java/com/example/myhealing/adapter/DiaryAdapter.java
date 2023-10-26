package com.example.myhealing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhealing.EmotionalDiary;
import com.example.myhealing.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder>{

    List<EmotionalDiary> diaryList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.titleText.setText("제목 : "+diaryList.get(position).title);
        holder.emotionText.setText("감정 : "+diaryList.get(position).emotion);
        holder.creationText.setText(diaryList.get(position).creationDate);
    }

    @Override
    public int getItemCount() {
        return this.diaryList.size();
    }

    //리스트 저장
    public void setDiaryList(List<EmotionalDiary> diaryList){
        if (diaryList != null) {
            // creationDate에 따라 리스트를 내림차순으로 정렬
            Collections.sort(diaryList, new Comparator<EmotionalDiary>() {
                @Override
                public int compare(EmotionalDiary diary1, EmotionalDiary diary2) {
                    // creationDate를 파싱하여 비교
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = sdf.parse(diary1.creationDate);
                        Date date2 = sdf.parse(diary2.creationDate);
                        return date2.compareTo(date1); // creationDate가 최신 순으로 정렬
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            this.diaryList = diaryList;
        } else {
            this.diaryList = new ArrayList<>();  // 빈 목록 생성
        }
        notifyDataSetChanged();  // 어댑터에 변경 사항 알리기
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleText, emotionText, creationText, CodeText, detailText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title_text);
            emotionText = itemView.findViewById(R.id.emotion_text);
            creationText = itemView.findViewById(R.id.creation_text);
        }
    }
}
