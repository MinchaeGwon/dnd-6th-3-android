package com.dnd.moneyroutine.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.R;
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
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ExpenditureWeeklyFragment extends Fragment {

    private PieChart pieChart;
    private String startDate;
    private String endDate;
    private String token;
    private int userId;
    private Calendar calendar;
    private SimpleDateFormat formatter;
    private SimpleDateFormat tvFormatter;
    private LocalDate now;

    private TextView tvShowWeekDate;
    private TextView tvWeekNum;


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
        setWeekNumber();
        setDate();
        setPieChart();


        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        startDate = formatter.format(getStartDay());
        endDate = formatter.format(getEndDay());

        Call<JsonObject> call = retroService.getWeeklyStatics(startDate, endDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("week", responseJson.toString());
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("week", t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(View v){
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);
        userId = JWTUtils.getUserId(token);

        pieChart=v.findViewById(R.id.pie_chart_week);
        tvShowWeekDate = v.findViewById(R.id.tv_start_end_week);
        tvWeekNum = v.findViewById(R.id.tv_week_num);
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        now=LocalDate.now();
    }

    private void setPieChart(){

        pieChart.setRotationAngle(-100); //시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); //차트 설명 제거
        pieChart.getLegend().setEnabled(false); //아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0,0,0,0); //차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); //가운데 hole
        pieChart.setHoleRadius(55f); //hole 크기 설정
        pieChart.setTransparentCircleRadius(0);

        ArrayList yValues = new ArrayList();

        yValues.add(new PieEntry(40f,"식비"));
        yValues.add(new PieEntry(20f,"교통비"));
        yValues.add(new PieEntry(10f,"나머지"));
        yValues.add(new PieEntry(30f,"유흥비"));


        PieDataSet dataSet = new PieDataSet(yValues,"Countries");

        //차트 색상 설정
        final int[] MY_COLORS = {Color.parseColor("#c896fa"), Color.parseColor("#a3bcff"),
                Color.parseColor("#7ae2f9"),Color.parseColor("#ced4da")};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for(int c: MY_COLORS) colors.add(c);
        dataSet.setColors(colors);

        pieChart.setDrawMarkers(false); //차트 색상 사이 간격
        pieChart.setDrawEntryLabels(false); //차트위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pieChart.setData(data);
    }

    private Date getStartDay(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);

//        if(calendar.DATE<7){
//            calendar.set(calendar.YEAR, calendar.MONTH, 1);
//        }
//        else{
//        }

        return calendar.getTime();
    }
    private Date getEndDay(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

//        if(calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)==calendar.get(Calendar.WEEK_OF_MONTH)){
//            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        }
//        if(calendar.DATE<7){
//            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY-calendar.DATE);
//        }

        return calendar.getTime();
    }

    private void setDate(){
        tvFormatter = new SimpleDateFormat("M.d");
        tvShowWeekDate.setText(tvFormatter.format(getStartDay())+"~"+tvFormatter.format(getEndDay()));
    }

    private void setWeekNumber(){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.setMinimalDaysInFirstWeek(7);
        tvWeekNum.setText(cal.get(Calendar.WEEK_OF_MONTH)+1+"주차");
    }







}