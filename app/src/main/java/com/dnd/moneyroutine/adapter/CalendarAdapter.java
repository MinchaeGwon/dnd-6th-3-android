package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dnd.moneyroutine.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// 지출 날짜 캘린더 gridview에 사용되는 adapter
public class CalendarAdapter extends ArrayAdapter<Date> {

    private LayoutInflater inflater;

    private int current_year; // 현재 년도
    private int current_month; // 현재 월

    public CalendarAdapter(@NonNull Context context, ArrayList<Date> days, int year, int month) {
        super(context, R.layout.item_day, days);
        inflater = LayoutInflater.from(context);

        this.current_year = year;
        this.current_month = month;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Date date = getItem(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        // today
        Calendar today = Calendar.getInstance();

        // inflate item if it does not exist yet
        if (view == null) {
            view = inflater.inflate(R.layout.item_day, parent, false);
        }

        ConstraintLayout con_back = view.findViewById(R.id.con_day);
        TextView tv_day = view.findViewById(R.id.item_day);

        // clear styling
        tv_day.setTextColor(Color.parseColor("#212529"));

        if (month != current_month || year != current_year) {
            con_back.setVisibility(View.INVISIBLE);
        } else if (month == (today.get(Calendar.MONTH) + 1) && year == today.get(Calendar.YEAR) && day == today.get(Calendar.DATE)) {
            // 오늘일 경우 view를 다르게 표시
            con_back.setBackgroundResource(R.drawable.circle_343a40);
            tv_day.setTextColor(Color.WHITE);
        }

        tv_day.setText(String.valueOf(calendar.get(Calendar.DATE)));

        return view;
    }
}
