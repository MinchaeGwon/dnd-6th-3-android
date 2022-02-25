package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.ExpenseDetailActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.ExpenditureDetail;
import com.dnd.moneyroutine.dto.ExpenditureEmotion;
import com.dnd.moneyroutine.dto.WeeklyDiary;
import com.dnd.moneyroutine.enums.EmotionEnum;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnSelectListener {
        void onSelect(View v, Date date);
    }

    private Context context;

    private ArrayList<Date> days;
    private int currentYear; // 현재 년도
    private int currentMonth; // 현재 월
    private int currentWeek; // 현재 주
    private ArrayList<WeeklyDiary> weeklyList; // 일주일 소비 감정

    private Date selectDate;
    private TextView selectDayView;

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public WeeklyCalendarAdapter(ArrayList<Date> days, int year, int month, int week, ArrayList<WeeklyDiary> weeklyList) {
        this.days = days;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentWeek = week;
        this.weeklyList = weeklyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_weekly_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DayViewHolder) {
            Date date = days.get(position);
            ((DayViewHolder) holder).setItem(date);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    private class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDatOfWeek;
        TextView tvDay;
        ImageView ivEmotion1;
        ImageView ivEmotion2;
        ImageView ivEmotion3;
        TextView tvEtcCnt;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDatOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvDay = itemView.findViewById(R.id.tv_weekly_day);
            ivEmotion1 = itemView.findViewById(R.id.iv_weekly_emotion1);
            ivEmotion2 = itemView.findViewById(R.id.iv_weekly_emotion2);
            ivEmotion3 = itemView.findViewById(R.id.iv_weekly_emotion3);
            tvEtcCnt = itemView.findViewById(R.id.tv_weekly_emotion_etc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 이미 선택된 날짜가 있을 경우 원상태로 변경
                    if (selectDate != null && selectDayView != null) {
                        selectDayView.setTextColor(Color.parseColor("#495057"));
                        selectDayView.setBackgroundColor(Color.parseColor("#F8F9FA"));
                    }

                    // 현재 선택된 날짜 색, 배경 변경
                    tvDay.setBackgroundResource(R.drawable.circle_343a40);
                    tvDay.setTextColor(Color.WHITE);

                    selectDate = days.get(getBindingAdapterPosition());
                    selectDayView = tvDay;

                    onSelectListener.onSelect(view, days.get(getBindingAdapterPosition()));
                }
            });
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int day = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            int week = calendar.get(Calendar.WEEK_OF_MONTH);

            // today
            Calendar today = Calendar.getInstance();

            ImageView[] ivArr= {ivEmotion1, ivEmotion2, ivEmotion3};

            // clear styling
            tvDay.setTextColor(Color.parseColor("#495057"));

            if (week != currentWeek || month != currentMonth || year != currentYear) {
                tvDay.setBackgroundColor(Color.parseColor("#F8F9FA"));
            } else if (month == (today.get(Calendar.MONTH) + 1) && year == today.get(Calendar.YEAR) && day == today.get(Calendar.DATE)) {
                // 오늘일 경우 view를 다르게 표시
                tvDay.setBackgroundResource(R.drawable.circle_343a40);
                tvDay.setTextColor(Color.WHITE);

                selectDate = date;
                selectDayView = tvDay;
            }

            tvDatOfWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            tvDay.setText(String.valueOf(calendar.get(Calendar.DATE)));

            // 소비 감정
            WeeklyDiary weeklyDiary = weeklyList.get(getBindingAdapterPosition());
            ArrayList<ExpenditureEmotion> expenditureList = weeklyDiary.getExpenditureList();
            for (int i = 0; i < expenditureList.size(); i++) {
                if (i < 3) {
                    setEmotion(ivArr[i], expenditureList.get(i).getEmotion());
                } else {
                    tvEtcCnt.setVisibility(View.VISIBLE);
                    tvEtcCnt.setText("+" + (expenditureList.size() - 3));
                }
            }
        }

        private void setEmotion(ImageView ivEmotion, EmotionEnum emotion) {
            ivEmotion.setVisibility(View.VISIBLE);
            ivEmotion.setImageResource(mappingEmotionIcon(emotion));
        }

        private int mappingEmotionIcon(EmotionEnum emotion) {
            switch (emotion) {
                case GOOD:
                    return R.drawable.icon_good;
                case SOSO:
                    return R.drawable.icon_soso;
                case BAD:
                    return R.drawable.icon_bad;
            }

            return -1;
        }

        private String getDayOfWeek(int day) {
            String dayOfWeek = "";
            switch (day) {
                case 1:
                    dayOfWeek = "일";
                    break;
                case 2:
                    dayOfWeek = "월";
                    break;
                case 3:
                    dayOfWeek = "화";
                    break;
                case 4:
                    dayOfWeek = "수";
                    break;
                case 5:
                    dayOfWeek = "목";
                    break;
                case 6:
                    dayOfWeek = "금";
                    break;
                case 7:
                    dayOfWeek = "토";
                    break;
            }

            return dayOfWeek;
        }
    }
}
