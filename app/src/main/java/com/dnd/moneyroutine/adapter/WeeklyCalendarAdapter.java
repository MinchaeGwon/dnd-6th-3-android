package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dnd.moneyroutine.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyCalendarAdapter extends ArrayAdapter<Date> {
    private LayoutInflater inflater;

    private int currentYear; // 현재 년도
    private int currentMonth; // 현재 월
    private int currentWeek; // 현재 주

    public WeeklyCalendarAdapter(@NonNull Context context, ArrayList<Date> days, int year, int month, int week) {
        super(context, R.layout.item_weekly_day, days);
        inflater = LayoutInflater.from(context);

        this.currentYear = year;
        this.currentMonth = month;
        this.currentWeek = week;
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

        TextView tvDay = view.findViewById(R.id.tv_weekly_day);
        ImageView ivFeel1 = view.findViewById(R.id.iv_weekly_feel1);
        ImageView ivFeel2 = view.findViewById(R.id.iv_weekly_feel2);
        ImageView ivFeel3 = view.findViewById(R.id.iv_weekly_feel3);
        TextView tvRestCnt = view.findViewById(R.id.tv_weekly_feel_rest);

        // clear styling
        tvDay.setTextColor(Color.parseColor("#495057"));

        if (week != currentWeek || month != currentMonth || year != currentYear) {
            tvDay.setBackgroundColor(Color.parseColor("#F8F9FA"));
        } else if (month == (today.get(Calendar.MONTH) + 1) && year == today.get(Calendar.YEAR) && day == today.get(Calendar.DATE)) {
            // 오늘일 경우 view를 다르게 표시
            tvDay.setBackgroundResource(R.drawable.circle_343a40);
            tvDay.setTextColor(Color.WHITE);
        }

        tvDay.setText(String.valueOf(calendar.get(Calendar.DATE)));

        return view;
    }
}
