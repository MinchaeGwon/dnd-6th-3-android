package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.ExpenditureEmotion;
import com.dnd.moneyroutine.dto.WeeklyDiary;
import com.dnd.moneyroutine.enums.EmotionEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyCalendarAdapter extends ArrayAdapter<Date> {
    private LayoutInflater inflater;

    private int currentYear; // 현재 년도
    private int currentMonth; // 현재 월
    private int currentWeek; // 현재 주
    private ArrayList<WeeklyDiary> weeklyList; // 일주일 소비 감정

    public WeeklyCalendarAdapter(@NonNull Context context, ArrayList<Date> days, int year, int month, int week, ArrayList<WeeklyDiary> weeklyList) {
        super(context, R.layout.item_weekly_day, days);
        inflater = LayoutInflater.from(context);

        this.currentYear = year;
        this.currentMonth = month;
        this.currentWeek = week;
        this.weeklyList = weeklyList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Date date = getItem(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);

        // today
        Calendar today = Calendar.getInstance();

        // inflate item if it does not exist yet
        if (view == null) {
            view = inflater.inflate(R.layout.item_weekly_day, parent, false);
        }

        TextView tvDatOfWeek = view.findViewById(R.id.tv_day_of_week);
        TextView tvDay = view.findViewById(R.id.tv_weekly_day);
        ImageView ivEmotion1 = view.findViewById(R.id.iv_weekly_emotion1);
        ImageView ivEmotion2 = view.findViewById(R.id.iv_weekly_emotion2);
        ImageView ivEmotion3 = view.findViewById(R.id.iv_weekly_emotion3);
        TextView tvEtcCnt = view.findViewById(R.id.tv_weekly_emotion_etc);

        ImageView[] ivArr= {ivEmotion1, ivEmotion2, ivEmotion3};

        // clear styling
        tvDay.setTextColor(Color.parseColor("#495057"));

        if (week != currentWeek || month != currentMonth || year != currentYear) {
            tvDay.setBackgroundColor(Color.parseColor("#F8F9FA"));
        } else if (month == (today.get(Calendar.MONTH) + 1) && year == today.get(Calendar.YEAR) && day == today.get(Calendar.DATE)) {
            // 오늘일 경우 view를 다르게 표시
            tvDay.setBackgroundResource(R.drawable.circle_343a40);
            tvDay.setTextColor(Color.WHITE);
        }

        tvDatOfWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        tvDay.setText(String.valueOf(calendar.get(Calendar.DATE)));

        WeeklyDiary weeklyDiary = weeklyList.get(position);
        ArrayList<ExpenditureEmotion> expenditureList = weeklyDiary.getExpenditureList();
        for (int i = 0; i < expenditureList.size(); i++) {
            if (i < 3) {
                setEmotion(ivArr[i], expenditureList.get(i).getEmotion());
            } else {
                tvEtcCnt.setVisibility(View.VISIBLE);
                tvEtcCnt.setText("+" + (expenditureList.size() - 3));
            }
        }

        return view;
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
