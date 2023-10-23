package com.example.myhealing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhealing.EmotionalDiary;
import com.example.myhealing.R;

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

        holder.titleText.setText(diaryList.get(position).title);
        holder.emotionText.setText(diaryList.get(position).emotion);
    }

    @Override
    public int getItemCount() {
        return this.diaryList.size();
    }

    //리스트 저장
    public void setUserList(List<EmotionalDiary> diaryList){

        this.diaryList = diaryList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleText, emotionText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title_text);
            emotionText = itemView.findViewById(R.id.emotion_text);
        }
    }
}
