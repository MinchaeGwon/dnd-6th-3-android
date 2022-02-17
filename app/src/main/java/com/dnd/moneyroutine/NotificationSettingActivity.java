package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.DayPickerDialog;
import com.dnd.moneyroutine.custom.TimePickerDialog;
import com.dnd.moneyroutine.custom.YearMonthPickerDialog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

// 알림 설정 activity
public class NotificationSettingActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private SwitchCompat scNoti;
    private LinearLayout btnTime;
    private LinearLayout btnDay;

    private TextView tvTime;
    private TextView tvDay;

    private int selectHour;
    private int selectMinute;
    private ArrayList<String> selectDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        initView();
        setListener();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_noti_back);
        scNoti = findViewById(R.id.sc_noti_switch);
        btnTime = findViewById(R.id.ll_noti_time);
        btnDay = findViewById(R.id.ll_noti_day);

        tvTime = findViewById(R.id.tv_noti_time);
        tvDay = findViewById(R.id.tv_noti_day);
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 시간 선택 다이얼로그 띄우기
                TimePickerDialog timePickerDialog = new TimePickerDialog(selectHour, selectMinute);
                timePickerDialog.show(NotificationSettingActivity.this.getSupportFragmentManager(), "TimePickerDialog");

                timePickerDialog.setOnSelectListener(new TimePickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(int hour, int minute) {
                        selectHour = hour;
                        selectMinute = minute;

                        tvTime.setText(hour + ":" + String.format("%02d", minute));
                    }
                });
            }
        });

        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectDays.size() == 0) {
                    selectDays.addAll(Arrays.asList(tvDay.getText().toString().split(",")));
                }

                // 요일 선택 다이얼로그 띄우기
                DayPickerDialog dayPickerDialog = new DayPickerDialog(selectDays);
                dayPickerDialog.show(NotificationSettingActivity.this.getSupportFragmentManager(), "DayPickerDialog");

                dayPickerDialog.setOnSelectListener(new DayPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(ArrayList<String> days) {
                        selectDays = days;

                        StringBuilder result = new StringBuilder();
                        for (int i = 0; i < days.size(); i++) {
                            result.append(days.get(i));

                            if (i < days.size() - 1) {
                                result.append(",");
                            }
                        }

                        tvDay.setText(result);
                    }
                });
            }
        });
    }
}