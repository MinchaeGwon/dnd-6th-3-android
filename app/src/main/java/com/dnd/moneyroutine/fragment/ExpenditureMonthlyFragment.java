package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.MonthlyDetailActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.GoalCategoryInfo;
import com.dnd.moneyroutine.dto.MonthlyStatistics;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.JWTUtils;
import com.dnd.moneyroutine.service.RetrofitService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ExpenditureMonthlyFragment extends Fragment {

    private String token;
    private int userId;

    private PieChart pieChart;
    private TextView tvMonth;
    private TextView tvDate;

    private Calendar calendar;
    private SimpleDateFormat formatter;
    private SimpleDateFormat tvFormatter;

    private LocalDate startDate;
    private LocalDate endDate;

    private ImageView ivPrevious;
    private ImageView ivNext;

    private ImageView ivDetail1;
    private ImageView ivDetail2;
    private ImageView ivDetail3;
    private ImageView ivDetail4;

    private TextView tvTop;
    private TextView tvTopRatio;
    private TextView totalExpenditure;

    private TextView tvCategoryName1;
    private TextView tvPercentage1;
    private TextView tvExpense1;
    private TextView tvCategoryName2;
    private TextView tvPercentage2;
    private TextView tvExpense2;
    private TextView tvCategoryName3;
    private TextView tvPercentage3;
    private TextView tvExpense3;
    private TextView tvCategoryName4;
    private TextView tvPercentage4;
    private TextView tvExpense4;

    private MonthlyStatistics responseMonthDetail;
    private ArrayList<GoalCategoryInfo> goalCategoryInfo;
    DecimalFormat dcFormat = new DecimalFormat("#,###");

    private LocalDate now;

    private int percentSum;
    private int expenseSum;

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
        setContent();
        showDetail();
        getMonthlyStatistics(startDate, endDate);
        getMonthlyTrend(now);

    }

    private void initView(View v) {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);
        userId = JWTUtils.getUserId(token);


        pieChart = v.findViewById(R.id.pie_chart_month);
        tvMonth = v.findViewById(R.id.tv_expenditure_month);
        tvDate = v.findViewById(R.id.tv_start_end_month);
        ivPrevious = v.findViewById(R.id.iv_previous_month);
        ivNext = v.findViewById(R.id.iv_next_month);

        tvCategoryName1 = v.findViewById(R.id.tv_first_category_month);
        tvCategoryName2 = v.findViewById(R.id.tv_second_category_month);
        tvCategoryName3 = v.findViewById(R.id.tv_third_category_month);
        tvCategoryName4 = v.findViewById(R.id.tv_else_category_month);

        tvPercentage1 = v.findViewById(R.id.tv_first_percent_month);
        tvPercentage2 = v.findViewById(R.id.tv_second_percent_month);
        tvPercentage3 = v.findViewById(R.id.tv_third_percent_month);
        tvPercentage4 = v.findViewById(R.id.tv_else_percent_month);

        tvExpense1 = v.findViewById(R.id.tv_first_amount_month);
        tvExpense2 = v.findViewById(R.id.tv_second_amount_month);
        tvExpense3 = v.findViewById(R.id.tv_third_amount_month);
        tvExpense4 = v.findViewById(R.id.tv_else_amount_month);

        ivDetail1 = v.findViewById(R.id.iv_detail1_month);
        ivDetail2 = v.findViewById(R.id.iv_detail2_month);
        ivDetail3 = v.findViewById(R.id.iv_detail3_month);
        ivDetail4 = v.findViewById(R.id.iv_detail4_month);

        tvTop = v.findViewById(R.id.tv_month_top_category);
        tvTopRatio = v.findViewById(R.id.tv_month_top_percent);
        totalExpenditure = v.findViewById(R.id.tv_total_month);

        calendar = Calendar.getInstance();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        tvFormatter = new SimpleDateFormat("M.d");
        now = LocalDate.now();

    }

    private void drawPieChart() {

        tvTop.setText(responseMonthDetail.getTopCategory());
        totalExpenditure.setText(responseMonthDetail.getTotalExpense().toString() + "원");

        pieChart.setRotationAngle(-100); //시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); //차트 설명 제거
        pieChart.getLegend().setEnabled(false); //아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0, 0, 0, 0); //차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); //가운데 hole
        pieChart.setHoleRadius(55f); //hole 크기 설정
        pieChart.setTransparentCircleRadius(0);

        goalCategoryInfo = new ArrayList<>();
        for (int i = 0; i < responseMonthDetail.getGoalCategoryInfoDtoList().size(); i++) {
//            String name = responseMonthDetail.getGoalCategoryInfoDtoList().get(i).getCategoryName();
//            double percentage  = responseMonthDetail.getGoalCategoryInfoDtoList().get(i).getPercentage();
//            long expense = responseMonthDetail.getGoalCategoryInfoDtoList().get(i).getExpense();
//
            goalCategoryInfo.add(responseMonthDetail.getGoalCategoryInfoDtoList().get(i));
        }

        Collections.sort(goalCategoryInfo, Collections.reverseOrder());


        percentSum = 0;
        expenseSum = 0;
        for (int i = 0; i < 3; i++) {
            expenseSum += goalCategoryInfo.get(i).getExpense();
            percentSum += goalCategoryInfo.get(i).getPercentage();
        }

        ArrayList values = new ArrayList();


        values.add(new PieEntry((float) goalCategoryInfo.get(0).getPercentage(), goalCategoryInfo.get(0).getCategoryName()));        values.add(new PieEntry((float)(100-expenseSum), "나머지"));
        values.add(new PieEntry((float)(100-percentSum), "나머지"));
        values.add(new PieEntry((float) goalCategoryInfo.get(2).getPercentage(), goalCategoryInfo.get(2).getCategoryName()));
        values.add(new PieEntry((float) goalCategoryInfo.get(1).getPercentage(), goalCategoryInfo.get(1).getCategoryName()));


        PieDataSet dataSet = new PieDataSet(values, "총 지출");

        //차트 색상 설정
        final int[] MY_COLORS = {Color.parseColor("#c896fa"), Color.parseColor("#ced4da"),
                Color.parseColor("#7ae2f9"), Color.parseColor("#a3bcff")};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        dataSet.setColors(colors);

        pieChart.setDrawMarkers(false); //차트 색상 사이 간격
        pieChart.setDrawEntryLabels(false); //차트위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pieChart.setData(data);
    }

    private void setContent() {
        tvCategoryName1.setText(goalCategoryInfo.get(0).getCategoryName());
        tvPercentage1.setText(goalCategoryInfo.get(0).getPercentage()+"%");
        tvExpense1.setText(dcFormat.format(goalCategoryInfo.get(0).getExpense())+"원");

        tvCategoryName2.setText(goalCategoryInfo.get(1).getCategoryName());
        tvPercentage2.setText(goalCategoryInfo.get(1).getPercentage()+"%");
        tvExpense2.setText(dcFormat.format(goalCategoryInfo.get(1).getExpense())+"원");

        tvCategoryName3.setText(goalCategoryInfo.get(2).getCategoryName());
        tvPercentage3.setText(goalCategoryInfo.get(2).getPercentage()+"%");
        tvExpense3.setText(dcFormat.format(goalCategoryInfo.get(2).getExpense())+"원");

        tvCategoryName4.setText(goalCategoryInfo.get(3).getCategoryName());
        tvPercentage4.setText(goalCategoryInfo.get(3).getPercentage()+"%");
        tvExpense4.setText(dcFormat.format(goalCategoryInfo.get(3).getExpense())+"원");

    }

    private void getMonthlyStatistics(LocalDate startDate, LocalDate endDate) {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMonthlyStatistics(startDate, endDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("month", responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
//                            JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                            Gson gson = new Gson();
                            responseMonthDetail = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<MonthlyDetailActivity>() {}.getType());
                        }
                        if(responseMonthDetail==null){

                        }
                    }
                } else {
                    Log.e("month", "error: " + response.code());
                    return;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("month", t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMonthlyTrend(LocalDate currentDate) {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMonthlyTrend(currentDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("monthly Trend", responseJson.toString());
                } else {
                    Log.e("monthly Trend", "error: " + response.code());
                    return;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("monthly Trend", t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setDate() {
        startDate = YearMonth.now().atDay(1);
        endDate = YearMonth.now().atEndOfMonth();

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate = startDate.plusMonths(1);
                LocalDate end = endDate.plusMonths(1);
                endDate = end.withDayOfMonth(end.lengthOfMonth());

                setTextView();
                getMonthlyStatistics(startDate, endDate);
                getMonthlyTrend(endDate);

            }
        });

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate = startDate.minusMonths(1);
                LocalDate end = endDate.minusMonths(1);
                endDate = end.withDayOfMonth(end.lengthOfMonth());
                setTextView();
                getMonthlyStatistics(startDate, endDate);
                getMonthlyTrend(endDate);
            }
        });

    }

    private void setTextView() {
//        tvMonth.setText(calendar.get(Calendar.MONTH)+1+ "월");
//        tvDate.setText(tvFormatter.format(getStartDay()) + "~" + tvFormatter.format(getEndDay()));
        String start = startDate.format(DateTimeFormatter.ofPattern("M.d"));
        String end = endDate.format(DateTimeFormatter.ofPattern("M.d"));
        tvMonth.setText(startDate.getYear() + ". " + startDate.getMonthValue() + "월");
        tvDate.setText(start + "-" + end);

    }



    private void showDetail() {

//        ivDetail1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
//                intent.putExtra("isElse",false);
//                intent.putExtra("category name",goalCategoryInfo.get(0).getCategoryName());
//                intent.putExtra("percentage", goalCategoryInfo.get(0).getPercentage());
//                intent.putExtra("expense", goalCategoryInfo.get(0).getExpense());
//                intent.putExtra("detail", (ArrayList)goalCategoryInfo.get(0).getWeeklyExpenditureDetailDtoList());
//                startActivity(intent);
//            }
//        });
//
//        ivDetail2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
//                intent.putExtra("isElse",false);
//                intent.putExtra("category name",goalCategoryInfo.get(1).getCategoryName());
//                intent.putExtra("percentage", goalCategoryInfo.get(1).getPercentage());
//                intent.putExtra("expense", goalCategoryInfo.get(1).getExpense());
//                intent.putExtra("detail", (ArrayList)goalCategoryInfo.get(1).getWeeklyExpenditureDetailDtoList());
//                startActivity(intent);
//            }
//        });
//
//        ivDetail3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
//                intent.putExtra("isElse",false);
//                intent.putExtra("category name",goalCategoryInfo.get(2).getCategoryName());
//                intent.putExtra("percentage", goalCategoryInfo.get(2).getPercentage());
//                intent.putExtra("expense", goalCategoryInfo.get(2).getExpense());
//                intent.putExtra("detail", (ArrayList)goalCategoryInfo.get(2).getWeeklyExpenditureDetailDtoList());
//                startActivity(intent);
//            }
//        });
//
//        ivDetail4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
//                intent.putExtra("isElse",true);
//                intent.putExtra("category name","나머지");
//                intent.putExtra("percentage", 100-percentSum);
//                intent.putExtra("expense", responseMonthDetail.getTotalExpense()-expenseSum);
//                for(int i=2; i<goalCategoryInfo.size();i++){
//                    intent.putExtra("detail"+(i-2), (ArrayList)goalCategoryInfo.get(i).getWeeklyExpenditureDetailDtoList());
//                }
//                startActivity(intent);
//            }
//        });

    }
}