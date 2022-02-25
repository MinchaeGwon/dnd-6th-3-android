package com.dnd.moneyroutine.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.WeeklyDetailAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.JWTUtils;
import com.dnd.moneyroutine.service.RetrofitService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ExpenditureWeeklyFragment extends Fragment {

    private PieChart pieChart;
    private LocalDate startDate;
    private LocalDate endDate;
    private String token;
    private int userId;
    private Calendar calendar;
    private SimpleDateFormat formatter;
    private SimpleDateFormat tvFormatter;
    private LocalDate now;

    private WeeklyDetailAdapter adapter;

    private InformationDialogFragment dialog;

    private TextView tvShowWeekDate;
    private TextView tvWeekNum;
    private ImageView ivPreviousWeek;
    private ImageView ivNextWeek;

    private ImageView ivDetail1;
    private ImageView ivDetail2;
    private ImageView ivDetail3;
    private ImageView ivDetail4;

    private RecyclerView rcDetai1;
    private RecyclerView rcDetai2;
    private RecyclerView rcDetai3;
    private RecyclerView rcDetai4;


    private ConstraintLayout c1Chart1;
    private ConstraintLayout c1Chart2;
    private ConstraintLayout c1Chart3;
    private ConstraintLayout c1Chart4;
    private ConstraintLayout c1Chart5;

    private LinearLayout info;

    private View chart1;
    private View chart2;
    private View chart3;
    private View chart4;
    private View chart5;


    public ExpenditureWeeklyFragment() {
        // Required empty public constructor
    }

    public static ExpenditureWeeklyFragment newInstance(String param1, String param2) {
        ExpenditureWeeklyFragment fragment = new ExpenditureWeeklyFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure_weekly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setRecyclerView();
        setWeekNumber();
        setDate();
        setWeekBtnListener();
        setPieChart();
        showDialog();
        getWeeklyStatistics(startDate, endDate);
        getWeeklyTrend(now);

    }

    private void initView(View v) {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);
        userId = JWTUtils.getUserId(token);

        calendar = Calendar.getInstance();

        pieChart = v.findViewById(R.id.pie_chart_week);
        tvShowWeekDate = v.findViewById(R.id.tv_start_end_week);
        tvWeekNum = v.findViewById(R.id.tv_week_num);
        formatter = new SimpleDateFormat("yyyy-MM-dd");


        adapter = new WeeklyDetailAdapter();

        ivPreviousWeek = v.findViewById(R.id.iv_previous_week);
        ivNextWeek = v.findViewById(R.id.iv_next_week);

        ivDetail1 = v.findViewById(R.id.iv_show_content_first);
        ivDetail2 = v.findViewById(R.id.iv_show_content_second);
        ivDetail3 = v.findViewById(R.id.iv_show_content_third);
        ivDetail4 = v.findViewById(R.id.iv_show_content_else);

        rcDetai1 = v.findViewById(R.id.rc_first_content_week);
        rcDetai2 = v.findViewById(R.id.rc_second_content_week);
        rcDetai3 = v.findViewById(R.id.rc_third_content_week);
        rcDetai4 = v.findViewById(R.id.rc_content_else_week);

        c1Chart1 = v.findViewById(R.id.cl_chart1_week);
        c1Chart2 = v.findViewById(R.id.cl_chart2_week);
        c1Chart3 = v.findViewById(R.id.cl_chart3_week);
        c1Chart4 = v.findViewById(R.id.cl_chart4_week);
        c1Chart5 = v.findViewById(R.id.cl_chart5_week);

        chart1 = v.findViewById(R.id.bar_chart_1);
        chart2 = v.findViewById(R.id.bar_chart_2);
        chart3 = v.findViewById(R.id.bar_chart_3);
        chart4 = v.findViewById(R.id.bar_chart_4);
        chart5 = v.findViewById(R.id.bar_chart_5);

        info = v.findViewById(R.id.ll_week_recommend);

        now = LocalDate.now();
    }

    private void setRecyclerView() {

        //첫번째
        rcDetai1.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcDetai1.setAdapter(adapter);

        ivDetail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcDetai1.getVisibility() == View.GONE) {
                    ivDetail1.setImageResource(R.drawable.icon_up);
                    rcDetai1.setVisibility(View.VISIBLE);
                } else if (rcDetai1.getVisibility() == View.VISIBLE) {
                    ivDetail1.setImageResource(R.drawable.icon_down);
                    rcDetai1.setVisibility(View.GONE);
                }
            }
        });


        //두번째
        rcDetai2.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcDetai2.setAdapter(adapter);

        ivDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcDetai2.getVisibility() == View.GONE) {
                    ivDetail2.setImageResource(R.drawable.icon_up);
                    rcDetai2.setVisibility(View.VISIBLE);
                } else if (rcDetai2.getVisibility() == View.VISIBLE) {
                    ivDetail2.setImageResource(R.drawable.icon_down);
                    rcDetai2.setVisibility(View.GONE);
                }
            }
        });


        //세번째
        rcDetai3.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcDetai3.setAdapter(adapter);

        ivDetail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcDetai3.getVisibility() == View.GONE) {
                    ivDetail3.setImageResource(R.drawable.icon_up);
                    rcDetai3.setVisibility(View.VISIBLE);
                } else if (rcDetai3.getVisibility() == View.VISIBLE) {
                    ivDetail3.setImageResource(R.drawable.icon_down);
                    rcDetai3.setVisibility(View.GONE);
                }
            }
        });

        //나머지
        rcDetai4.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcDetai4.setAdapter(adapter);

        ivDetail4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcDetai4.getVisibility() == View.GONE) {
                    ivDetail4.setImageResource(R.drawable.icon_up);
                    rcDetai4.setVisibility(View.VISIBLE);
                } else if (rcDetai4.getVisibility() == View.VISIBLE) {
                    ivDetail4.setImageResource(R.drawable.icon_down);
                    rcDetai4.setVisibility(View.GONE);
                }
            }
        });

    }

    private void getWeeklyStatistics(LocalDate startDate, LocalDate endDate){
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getWeeklyStatistics(startDate, endDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("week", responseJson.toString());
                } else {
                    Log.e("week", "error: " + response.code());

                    return;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("week", "failure"+ t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeeklyTrend(LocalDate currentDate){
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getWeeklyTrend(currentDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("week", responseJson.toString());
                } else {
                    Log.e("week", "error: " + response.code());

                    return;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("week", "failure"+ t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPieChart() {

        pieChart.setRotationAngle(-100); //시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); //차트 설명 제거
        pieChart.getLegend().setEnabled(false); //아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0, 0, 0, 0); //차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); //가운데 hole
        pieChart.setHoleRadius(55f); //hole 크기 설정
        pieChart.setTransparentCircleRadius(0);

        ArrayList values = new ArrayList();

        values.add(new PieEntry(40f, "식비"));
        values.add(new PieEntry(20f, "교통비"));
        values.add(new PieEntry(10f, "나머지"));
        values.add(new PieEntry(30f, "유흥비"));


        PieDataSet dataSet = new PieDataSet(values, "Countries");

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


    private void setDate() {

        switch (now.getDayOfWeek()) {
            case SUNDAY:
                startDate = now.minusDays(6);
                break;
            case MONDAY:
                startDate = now;
                break;
            case TUESDAY:
                startDate = now.minusDays(1);
                break;
            case WEDNESDAY:
                startDate = now.minusDays(2);
                break;
            case THURSDAY:
                startDate = now.minusDays(3);
                break;
            case FRIDAY:
                startDate = now.minusDays(4);
                break;
            case SATURDAY:
                startDate = now.minusDays(5);
                break;

        }

        endDate = startDate.plusDays(6);

        //textview에 설정
        setWeekTextView();
    }

    private void setWeekBtnListener(){
        ivNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate=startDate.plusDays(7);
                endDate=endDate.plusDays(7);
                calendar.add(Calendar.DATE,7);
                setWeekTextView();
                setWeekNumber();
                getWeeklyStatistics(startDate, endDate);
                getWeeklyTrend(endDate);
            }
        });

        ivPreviousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate=startDate.minusDays(7);
                endDate=endDate.minusDays(7);
                calendar.add(Calendar.DATE,-7);

                setWeekTextView();
                setWeekNumber();
                getWeeklyStatistics(startDate, endDate);
                getWeeklyTrend(endDate);
            }
        });
    }

    private void setWeekTextView(){
        tvShowWeekDate.setText(startDate.getMonthValue() + "." + startDate.getDayOfMonth() + "-" + endDate.getMonthValue() + "." + endDate.getDayOfMonth());

    }

    private void setWeekNumber() {
        //몇번째 주인지 가져오기
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(7);
        tvWeekNum.setText(calendar.get(Calendar.WEEK_OF_MONTH)  + "주차");
    }

    private void showDialog() { //주별 권장 지출액 다이얼로그
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new InformationDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "information");

            }
        });
    }


}