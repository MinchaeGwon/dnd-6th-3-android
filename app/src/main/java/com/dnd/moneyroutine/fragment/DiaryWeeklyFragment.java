package com.dnd.moneyroutine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.WeeklyCalendarAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryWeeklyFragment extends Fragment {

    private static final String TAG = "DiaryWeekly";
    private static final int DAYS_COUNT = 7;

    private ImageButton ibPrev;
    private ImageButton ibNext;
    private TextView tvHeader;
    private GridView gvCalendar;

    private TextView tvDate;

    private LinearLayout btnGood;
    private TextView tvGoodCnt;
    private TextView tvGoodMoney;
    private ImageView ivGoodMore;
    private RecyclerView rvGood;

    private LinearLayout btnSoso;
    private TextView tvSosoCnt;
    private TextView tvSosoMoney;
    private ImageView ivSosoMore;
    private RecyclerView rvSoso;

    private LinearLayout btnBad;
    private TextView tvBadCnt;
    private TextView tvBadMoney;
    private ImageView ivBadMore;
    private RecyclerView rvBad;

    private Calendar currentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_weekly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initField();
        setListener();

        setCalendar();
    }

    private void initView(View v) {
        ibPrev = v.findViewById(R.id.ib_week_prev);
        ibNext = v.findViewById(R.id.ib_week_next);
        tvHeader = v.findViewById(R.id.tv_week_header);
        gvCalendar = v.findViewById(R.id.gv_weekly_calendar);

        tvDate = v.findViewById(R.id.tv_week_date);

        btnGood = v.findViewById(R.id.ll_week_good);
        tvGoodCnt = v.findViewById(R.id.tv_week_good_cnt);
        tvGoodMoney = v.findViewById(R.id.tv_week_good_money);
        ivGoodMore = v.findViewById(R.id.iv_week_good_more);
        rvGood = v.findViewById(R.id.rv_week_good_detail);

        btnSoso = v.findViewById(R.id.ll_week_soso);
        tvSosoCnt = v.findViewById(R.id.tv_week_soso_cnt);
        tvSosoMoney = v.findViewById(R.id.tv_week_soso_money);
        ivSosoMore = v.findViewById(R.id.iv_week_soso_more);
        rvSoso = v.findViewById(R.id.rv_week_soso_detail);

        btnBad = v.findViewById(R.id.ll_week_bad);
        tvBadCnt = v.findViewById(R.id.tv_week_bad_cnt);
        tvBadMoney = v.findViewById(R.id.tv_week_bad_money);
        ivBadMore = v.findViewById(R.id.iv_week_bad_more);
        rvBad = v.findViewById(R.id.rv_week_bad_detail);
    }

    private void initField() {
        currentDate = Calendar.getInstance();
        tvDate.setText((currentDate.get(Calendar.MONTH) + 1) + "월 " + currentDate.get(Calendar.DATE) + "일 소비 다이어리");
    }

    private void setListener() {
        ibPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate.add(Calendar.WEEK_OF_MONTH, -1);
                setCalendar();
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.WEEK_OF_MONTH, 1);
                setCalendar();
            }
        });

        gvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Date date = (Date) adapterView.getItemAtPosition(position);

                Calendar selectDate = Calendar.getInstance();
                selectDate.setTime(date);

                tvDate.setText((selectDate.get(Calendar.MONTH) + 1) + "월 " + selectDate.get(Calendar.DATE) + "일 소비 다이어리");
            }
        });

        btnGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvGood.getVisibility() == View.GONE) {
                    rvGood.setVisibility(View.VISIBLE);
                    ivGoodMore.setRotationX(180);
                } else {
                    rvGood.setVisibility(View.GONE);
                    ivGoodMore.setRotationX(360);
                }
            }
        });

        btnSoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvSoso.getVisibility() == View.GONE) {
                    rvSoso.setVisibility(View.VISIBLE);
                    ivSosoMore.setRotationX(180);
                } else {
                    rvSoso.setVisibility(View.GONE);
                    ivSosoMore.setRotationX(360);
                }
            }
        });

        btnBad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvBad.getVisibility() == View.GONE) {
                    rvBad.setVisibility(View.VISIBLE);
                    ivBadMore.setRotationX(180);
                } else {
                    rvBad.setVisibility(View.GONE);
                    ivBadMore.setRotationX(360);
                }
            }
        });
    }

    // 캘린더 설정
    public void setCalendar() {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();

        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH) + 1;
        int week = currentDate.get(Calendar.WEEK_OF_MONTH);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작을 일요일로 설정
        int weekBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        calendar.add(Calendar.DAY_OF_MONTH, -weekBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK,1);
        }

        // update view
        WeeklyCalendarAdapter weeklyCalendarAdapter = new WeeklyCalendarAdapter(getContext(), cells, year, month, week);
        gvCalendar.setAdapter(weeklyCalendarAdapter);

        // update title
        tvHeader.setText(month + "월 " + week + "주차");
    }
}
