package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.DailyDiary;
import com.dnd.moneyroutine.enums.EmotionEnum;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WeeklyEmotionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DailyDiary> dailyList;

    public WeeklyEmotionAdapter(ArrayList<DailyDiary> dailyList) {
        this.dailyList = dailyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_emotion, parent, false);
        return new EmotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmotionViewHolder) {
            DailyDiary daily = dailyList.get(position);
            ((EmotionViewHolder) holder).setItem(daily);
        }
    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    private class EmotionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEmotionIcon;
        TextView tvEmotionName;
        TextView tvEmotionCnt;
        TextView tvEmotionExpense;
        ImageView ivHold;
        ImageView ivMore;
        RecyclerView rvExpense;

        public EmotionViewHolder(@NonNull View itemView) {
            super(itemView);

            ivEmotionIcon = itemView.findViewById(R.id.iv_week_emotion_icon);
            tvEmotionName = itemView.findViewById(R.id.tv_week_emotion_name);
            tvEmotionCnt = itemView.findViewById(R.id.tv_week_emotion_cnt);
            tvEmotionExpense = itemView.findViewById(R.id.tv_week_emotion_money);
            ivHold = itemView.findViewById(R.id.iv_week_emotion_hold);
            ivMore = itemView.findViewById(R.id.iv_week_emotion_more);
            rvExpense = itemView.findViewById(R.id.rv_week_emotion_detail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rvExpense.getVisibility() == View.GONE) {
                        rvExpense.setVisibility(View.VISIBLE);
                        ivHold.setVisibility(View.VISIBLE);
                        ivMore.setVisibility(View.GONE);
                    } else {
                        rvExpense.setVisibility(View.GONE);
                        ivHold.setVisibility(View.GONE);
                        ivMore.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(DailyDiary daily) {
            setEmotionInfo(daily.getEmotion());

            tvEmotionName.setText(daily.getEmotion().getName());
            tvEmotionCnt.setText(daily.getCount() + "개");

            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String expense = "총 " + decimalFormat.format(daily.getAmount()) + "원";
            tvEmotionExpense.setText(expense);

            WeeklyDiaryAdapter weeklyDiaryAdapter = new WeeklyDiaryAdapter(daily.getEmotion(), daily.getExpenditureList());
            rvExpense.setAdapter(weeklyDiaryAdapter);
            rvExpense.setLayoutManager(new LinearLayoutManager(context));
        }

        private void setEmotionInfo(EmotionEnum emotion) {
            switch (emotion) {
                case GOOD:
                    ivEmotionIcon.setImageResource(R.drawable.icon_good);
                    tvEmotionName.setTextColor(Color.parseColor("#107D69"));
                    tvEmotionExpense.setTextColor(Color.parseColor("#107D69"));
                    break;
                case SOSO:
                    ivEmotionIcon.setImageResource(R.drawable.icon_soso);
                    tvEmotionName.setTextColor(Color.parseColor("#1E5CA4"));
                    tvEmotionExpense.setTextColor(Color.parseColor("#1E5CA4"));
                    break;
                case BAD:
                    ivEmotionIcon.setImageResource(R.drawable.icon_bad);
                    tvEmotionName.setTextColor(Color.parseColor("#D13474"));
                    tvEmotionExpense.setTextColor(Color.parseColor("#D13474"));
                    break;
            }
        }
    }
}
