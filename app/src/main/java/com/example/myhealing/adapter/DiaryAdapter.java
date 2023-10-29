package com.example.myhealing.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhealing.EmotionalDiary;
import com.example.myhealing.R;
import com.example.myhealing.ShowDiaryActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder> {

    // 다양한 정렬 방식을 나타내는 상수
    public static final int SORT_RECENT = 0;  // 최근순
    public static final int SORT_OLD = 1;     // 오래된순
    public static final int SORT_DATE_ASC = 2; // 날짜 오름차순
    public static final int SORT_DATE_DESC = 3; // 날짜 내림차순

    private List<EmotionalDiary> diaryList;
    private Context context;
    private int sortingOption = SORT_RECENT; // 기본값으로 SORT_RECENT를 선택

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_SORTING_OPTION = "sorting_option";

    public DiaryAdapter(Context context) {
        this.context = context;
        this.diaryList = new ArrayList<>(); // 이 부분을 추가
        // SharedPreferences에서 저장된 정렬 설정을 불러옴
        sortingOption = loadSortingOption();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int mPosition = holder.getAdapterPosition();

        // 선택한 정렬 옵션에 따라 diaryList를 정렬합니다.
        switch (sortingOption) {
            case SORT_RECENT:
                Collections.sort(diaryList, new Comparator<EmotionalDiary>() {
                    @Override
                    public int compare(EmotionalDiary diary1, EmotionalDiary diary2) {
                        return Integer.compare(diary2.diaryCode, diary1.diaryCode);
                    }
                });
                break;
            case SORT_OLD:
                Collections.sort(diaryList, new Comparator<EmotionalDiary>() {
                    @Override
                    public int compare(EmotionalDiary diary1, EmotionalDiary diary2) {
                        return Integer.compare(diary1.diaryCode, diary2.diaryCode);
                    }
                });
                break;
            case SORT_DATE_ASC:
                Collections.sort(diaryList, new Comparator<EmotionalDiary>() {
                    @Override
                    public int compare(EmotionalDiary diary1, EmotionalDiary diary2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = sdf.parse(diary1.creationDate);
                            Date date2 = sdf.parse(diary2.creationDate);
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                break;
            case SORT_DATE_DESC:
                Collections.sort(diaryList, new Comparator<EmotionalDiary>() {
                    @Override
                    public int compare(EmotionalDiary diary1, EmotionalDiary diary2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = sdf.parse(diary1.creationDate);
                            Date date2 = sdf.parse(diary2.creationDate);
                            return date2.compareTo(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                break;
        }

        holder.titleText.setText("제목: " + diaryList.get(mPosition).title);
        holder.emotionText.setText("감정: " + diaryList.get(mPosition).emotion);
        holder.creationText.setText(diaryList.get(mPosition).creationDate);

        // ShowDiaryActivity를 여는 클릭 리스너를 추가합니다.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowDiaryActivity.class);
                intent.putExtra("diaryCode", diaryList.get(mPosition).diaryCode);
                intent.putExtra("title", diaryList.get(mPosition).title);
                intent.putExtra("emotion", diaryList.get(mPosition).emotion);
                intent.putExtra("creationDate", diaryList.get(mPosition).creationDate);
                intent.putExtra("detail", diaryList.get(mPosition).detail);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public void setDiaryList(List<EmotionalDiary> diaryList) {
        if (diaryList != null) {
            this.diaryList = diaryList;
        } else {
            this.diaryList = new ArrayList<>();  // 빈 목록 생성
        }
        notifyDataSetChanged();  // 어댑터에 변경 사항 알리기
    }

    public void setSortingOption(int option) {
        sortingOption = option;
        saveSortingOption(option);
//        notifyDataSetChanged(); // 어댑터에 새로운 정렬 옵션을 기반으로 데이터를 다시 바인딩하도록 알립니다
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, emotionText, creationText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title_text);
            emotionText = itemView.findViewById(R.id.emotion_text);
            creationText = itemView.findViewById(R.id.creation_text);
        }
    }

    private void saveSortingOption(int option) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PREF_SORTING_OPTION, option);
        editor.apply();
    }

    private int loadSortingOption() {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(PREF_SORTING_OPTION, SORT_RECENT);
    }
}
