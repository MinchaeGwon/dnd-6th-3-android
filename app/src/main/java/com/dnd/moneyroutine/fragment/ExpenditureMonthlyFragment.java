package com.dnd.moneyroutine.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnd.moneyroutine.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ExpenditureMonthlyFragment extends Fragment {

    private PieChart pieChart;
    private TextView tvMonth;
    private TextView tvDate;

    private Calendar calendar;
    private SimpleDateFormat formatter;
    private SimpleDateFormat tvFormatter;

    private ImageView ivPrevious;
    private ImageView ivNext;

    private LocalDate now;

    public ExpenditureMonthlyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure_monthly, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setDate();
        setTextView();
        drawPieChart();
        setMonth();
    }

    private void initView(View v) {
        pieChart = v.findViewById(R.id.pie_chart_month);
        tvMonth = v.findViewById(R.id.tv_expenditure_month);
        tvDate = v.findViewById(R.id.tv_start_end_month);
        ivPrevious = v.findViewById(R.id.iv_previous_month);
        ivNext = v.findViewById(R.id.iv_next_month);

        calendar = Calendar.getInstance();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        tvFormatter = new SimpleDateFormat("M.d");
        now = LocalDate.now();

    }

    private void drawPieChart() {

        pieChart.setRotationAngle(-100); //시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); //차트 설명 제거
        pieChart.getLegend().setEnabled(false); //아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0, 0, 0, 0); //차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); //가운데 hole
        pieChart.setHoleRadius(55f); //hole 크기 설정
        pieChart.setTransparentCircleRadius(0);

        ArrayList yValues = new ArrayList();

        yValues.add(new PieEntry(40f, "식비"));
        yValues.add(new PieEntry(20f, "교통비"));
        yValues.add(new PieEntry(10f, "나머지"));
        yValues.add(new PieEntry(30f, "유흥비"));


        PieDataSet dataSet = new PieDataSet(yValues, "Countries");

        //차트 색상 설정
        final int[] MY_COLORS = {Color.parseColor("#c896fa"), Color.parseColor("#a3bcff"),
                Color.parseColor("#7ae2f9"), Color.parseColor("#ced4da")};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        dataSet.setColors(colors);

        pieChart.setDrawMarkers(false); //차트 색상 사이 간격
        pieChart.setDrawEntryLabels(false); //차트위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pieChart.setData(data);
    }

    private Date getStartDay() {
        calendar.set(calendar.YEAR, calendar.MONTH, 1);
        return calendar.getTime();
    }

    private Date getEndDay() {
        calendar.set(calendar.YEAR, calendar.MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();

    }

    private Date setDate(){
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH,1);
                tvMonth.setText(calendar.MONTH+ "월");
            }
        });
        return  calendar.getTime();
    }

    private void setTextView() {
        tvMonth.setText(calendar.MONTH+ "월");
        tvDate.setText(tvFormatter.format(getStartDay()) + "~" + tvFormatter.format(getEndDay()));
    }

    private void setMonth(){


    }
}