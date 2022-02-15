package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EnterExpenditureActivity extends AppCompatActivity {

    private ConstraintLayout enterDate;
    private TextView date;

    private Date currentTime;

    private SimpleDateFormat monthFormat;
    private SimpleDateFormat dayFormat;
    private SimpleDateFormat weekdayFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_expenditure);

        initView();
        setDate();
    }

    private void initView() {
        enterDate = findViewById(R.id.cl_enter_date);
        date = findViewById(R.id.tv_spend_date);

    }

    //소비 날짜 선택
    private void setDate() {

        //textview에 오늘 날짜 표시
        currentTime = Calendar.getInstance().getTime();

        monthFormat = new SimpleDateFormat("M", Locale.getDefault());
        dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());

        String month = monthFormat.format(currentTime);
        String day = dayFormat.format(currentTime);
        String weekday = weekdayFormat.format(currentTime);

        switch(weekday){
            case "Mon":
                weekday="월";
                break;

            case "Tue":
                weekday="화";
                break;

            case "Wed":
                weekday="수";
                break;

            case "Thu":
                weekday="목";
                break;

            case "Fri":
                weekday="금";
                break;

            case "Sat":
                weekday="토";
                break;

            case "Sun":
                weekday="일";
                break;
        }

        date.setText(month+"월 "+day+"일 "+weekday+"요일");

        enterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarFragment calendarFragment = CalendarFragment.getInstance();
                calendarFragment.show(getSupportFragmentManager(), "Calendar");

            }
        });
    }


}