package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.MonthlyDetailActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.Exclude;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.ExpenditureDetailDto;
import com.dnd.moneyroutine.dto.GoalCategoryInfo;
import com.dnd.moneyroutine.dto.MonthlyExpense;
import com.dnd.moneyroutine.dto.MonthlyStatistics;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.JWTUtils;
import com.dnd.moneyroutine.service.RetrofitService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
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
import java.util.List;

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

    private TextView tvNotice; //이번주 지출이 위험해요
    private TextView tvDifference; //얼마 초과 됐어요
    private TextView tvMonthBudgetBarChart; //차트 아래 예산
    private TextView tvPossibleText; // 총지출 or 쓸 수 있는 돈
    private TextView tvPossibleAmount; //총지출 쓸 수 있는 돈 숫자
    private TextView tvRemainText; // 아낀 돈 or 초과된 돈
    private TextView tvRemainAmount;//아낀 돈 초과된 돈 숫자

    private View viewBarChart1;
    private View viewBarChart2;
    private View viewBarChart3;
    private View viewBarChart4;
    private View viewBarChart5;

    private TextView tvBarChartMonth1;
    private TextView tvBarChartMonth2;
    private TextView tvBarChartMonth3;
    private TextView tvBarChartMonth4;
    private TextView tvBarChartMonth5;


    private MonthlyStatistics responseMonthDetail;
    private ArrayList<GoalCategoryInfo> goal;
    DecimalFormat dcFormat = new DecimalFormat("#,###");

    private ExpenditureDetailDto expenditureDetailDto;

    private List<MonthlyExpense> monthlyTrend;

    private LocalDate now;

    private int percentSum;
    private int expenseSum;
    private int monthDifference;

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
//        setTextView();
//        drawPieChart();
//        setContent();
//        drawBarChart();
        showDetail();
//        drawBarChart();
        setListener();
        getMonthlyStatistics(startDate, endDate);
        getMonthlyTrend(now);
//        drawPieChart();
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

        tvNotice = v.findViewById(R.id.tv_monthly_notice);
        tvDifference = v.findViewById(R.id.tv_monthly_difference);
        tvMonthBudgetBarChart = v.findViewById(R.id.tv_month_budget_bar_chart);
        tvPossibleText = v.findViewById(R.id.tv_month_possible_text);
        tvPossibleAmount = v.findViewById(R.id.tv_month_possible_amount);
        tvRemainText = v.findViewById(R.id.tv_remain_text);
        tvRemainAmount = v.findViewById(R.id.tv_remain_amount);

        viewBarChart1 = v.findViewById(R.id.view_monthly_bar_chart_1);
        viewBarChart2 = v.findViewById(R.id.view_monthly_bar_chart_2);
        viewBarChart3 = v.findViewById(R.id.view_monthly_bar_chart_3);
        viewBarChart4 = v.findViewById(R.id.view_monthly_bar_chart_4);
        viewBarChart5 = v.findViewById(R.id.view_monthly_bar_chart_5);

        tvBarChartMonth1 = v.findViewById(R.id.tv_month_bar_chart_text1);
        tvBarChartMonth2 = v.findViewById(R.id.tv_month_bar_chart_text2);
        tvBarChartMonth3 = v.findViewById(R.id.tv_month_bar_chart_text3);
        tvBarChartMonth4 = v.findViewById(R.id.tv_month_bar_chart_text4);
        tvBarChartMonth5 = v.findViewById(R.id.tv_month_bar_chart_text5);

        calendar = Calendar.getInstance();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        tvFormatter = new SimpleDateFormat("M.d");
        now = LocalDate.now();

    }

    private void drawPieChart() {

        totalExpenditure.setText(dcFormat.format(responseMonthDetail.getTotalExpense()) + "원");

        pieChart.setRotationAngle(-100); //시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); //차트 설명 제거
        pieChart.getLegend().setEnabled(false); //아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0, 0, 0, 0); //차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); //가운데 hole
        pieChart.setHoleRadius(55f); //hole 크기 설정
        pieChart.setTransparentCircleRadius(0);

        goal = new ArrayList<>();
        for (int i = 0; i < responseMonthDetail.getGoalCategoryInfoDtoList().size(); i++) {
//            String name = responseMonthDetail.getGoalCategoryInfoDtoList().get(i).getCategoryName();
//            double percentage  = responseMonthDetail.getGoalCategoryInfoDtoList().get(i).getPercentage();
//            long expense = responseMonthDetail.getGoalCategoryInfoDtoList().get(i).getExpense();
//
            goal.add(responseMonthDetail.getGoalCategoryInfoDtoList().get(i));
        }

        Collections.sort(goal, Collections.reverseOrder());
        tvTop.setText(goal.get(0).getCategoryName());
        tvTopRatio.setText(goal.get(0).getPercentage() + "%");

        int p1 =goal.get(0).getExpense()/responseMonthDetail.getTotalExpense()*100;
        int p2 =goal.get(1).getExpense()/responseMonthDetail.getTotalExpense()*100;
        int p3 =goal.get(2).getExpense()/responseMonthDetail.getTotalExpense()*100;


        percentSum = 0;
        expenseSum = 0;

        if (goal.size() < 4) {
            for (int i = 0; i < goal.size(); i++) {
                expenseSum += goal.get(i).getExpense();
                percentSum += goal.get(i).getPercentage();
            }
        } else {
            for (int i = 0; i < 3; i++) {
                expenseSum += goal.get(i).getExpense();
                percentSum += goal.get(i).getPercentage();
            }
        }


        ArrayList<PieEntry> values = new ArrayList();

//        values.add(new PieEntry(goal.get(0).getPercentage(), goal.get(0).getCategoryName()));
//        values.add(new PieEntry((100 - percentSum), "나머지"));
//        values.add(new PieEntry(goal.get(2).getPercentage(), goal.get(2).getCategoryName()));
//        values.add(new PieEntry(goal.get(1).getPercentage(), goal.get(1).getCategoryName()));

        values.add(new PieEntry(60f,"1"));
        values.add(new PieEntry(0, "나머지"));
        values.add(new PieEntry(0,"3"));
        values.add(new PieEntry(40f,"2"));


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
        tvCategoryName1.setText(goal.get(0).getCategoryName());
//        tvPercentage1.setText(goal.get(0).getPercentage() + "%");
        tvPercentage1.setText(60 + "%");
        tvExpense1.setText(dcFormat.format(goal.get(0).getExpense()) + "원");

        tvCategoryName2.setText(goal.get(1).getCategoryName());
//        tvPercentage2.setText(goal.get(1).getPercentage() + "%");
        tvPercentage2.setText(40 + "%");
        tvExpense2.setText(dcFormat.format(goal.get(1).getExpense()) + "원");

        tvCategoryName3.setText(goal.get(2).getCategoryName());
        tvPercentage3.setText(goal.get(2).getPercentage() + "%");
        tvExpense3.setText(dcFormat.format(goal.get(2).getExpense()) + "원");

        tvCategoryName4.setText("나머지");
        tvPercentage4.setText(goal.get(3).getPercentage() + "%");
        tvExpense4.setText(dcFormat.format(goal.get(3).getExpense()) + "원");

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
//                            Gson gson = new Gson();

//                            Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(ex).addSerializationExclusionStrategy(ex).create();
                            Gson gson = new Gson();
                            responseMonthDetail = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<MonthlyStatistics>() {}.getType());
                            drawPieChart();
                            setTextView();
                            setContent();
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

                    Gson gson = new Gson();
                    monthlyTrend = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<MonthlyDetailActivity>() {
                    }.getType());
//                    if (responseJson.get("statusCode").getAsInt() == 200) {
//                        if (!responseJson.get("data").isJsonNull()) {
////                            JsonArray jsonArray = responseJson.get("data").getAsJsonArray();
//
//                        }
//
//                    }

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
                drawPieChart();
                setContent();

//                setMonthlyTrendText();

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
                drawPieChart();
                setContent();

                if (now.getMonthValue() > startDate.getMonthValue()) {
//                    setMonthlyTrendText();
                }
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

//        if(responseMonthDetail.getTotalExpense()>responseMonthDetail.)

    }



//    private void drawBarChart() {
//
//        int overHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74, getResources().getDisplayMetrics());
//        int lessHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 121, getResources().getDisplayMetrics());
//        int nowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 169, getResources().getDisplayMetrics());
//
//
//        if (monthlyTrend.get(0) != null) {
//            tvBarChartMonth1.setText(monthlyTrend.get(0).getMonth() + "월");
//            if (monthlyTrend.get(0).getMonthExpense() > monthlyTrend.get(0).getBudget()) {
//                viewBarChart1.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, overHeight));
//
//            } else {
//                viewBarChart1.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lessHeight));
//            }
//        }
//
//        if (monthlyTrend.get(1) != null) {
//            tvBarChartMonth1.setText(monthlyTrend.get(1).getMonth() + "월");
//            if (monthlyTrend.get(1).getMonthExpense() > monthlyTrend.get(1).getBudget()) {
//                viewBarChart2.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, overHeight));
//
//            } else {
//                viewBarChart2.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lessHeight));
//            }
//        }
//
//        if (monthlyTrend.get(2) != null) {
//            tvBarChartMonth1.setText(monthlyTrend.get(2).getMonth() + "월");
//            if (monthlyTrend.get(2).getMonthExpense() > monthlyTrend.get(2).getBudget()) {
//                viewBarChart3.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, overHeight));
//
//            } else {
//                viewBarChart3.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lessHeight));
//            }
//
//        }
//
//        if (monthlyTrend.get(3) != null) {
//            tvBarChartMonth1.setText(monthlyTrend.get(3).getMonth() + "월");
//            if (monthlyTrend.get(3).getMonthExpense() > monthlyTrend.get(3).getBudget()) {
//                viewBarChart4.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, overHeight));
//
//            } else {
//                viewBarChart4.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lessHeight));
//            }
//        }
//
//        if (monthlyTrend.get(4) != null) {
//            tvBarChartMonth1.setText(monthlyTrend.get(4).getMonth() + "월");
//            viewBarChart5.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nowHeight));
//            if (monthlyTrend.get(4).getMonthExpense() > monthlyTrend.get(4).getBudget()) {
//                tvBarChartMonth1.setBackgroundResource(R.drawable.bar_chart_now_high);
//                tvNotice.setText("이번달 지출이 위험해요");
//
//            } else {
//                tvBarChartMonth1.setBackgroundResource(R.drawable.bar_chart_now_low);
//
//                tvNotice.setText("이번달 지출이 적당해요");
//
//
//                tvDifference.setText("아직" + formatter.format(monthlyTrend.get(4).getBudget() - monthlyTrend.get(4).getMonthExpense()) + "원 더 쓸 수 있어요");
//                tvDifference.setTextColor(Color.parseColor("#107D69"));
//
//                tvMonthBudgetBarChart.setText(formatter.format(monthlyTrend.get(4).getBudget()) + "원");
//
//                tvPossibleText.setText("쓸 수 있는 돈");
//                tvPossibleAmount.setText(formatter.format(monthlyTrend.get(4).getBudget() - monthlyTrend.get(4).getMonthExpense()) + "원");
//                tvPossibleAmount.setTextColor(Color.parseColor("#107D6"));
//
//                tvRemainText.setVisibility(View.INVISIBLE);
//                tvRemainAmount.setVisibility(View.INVISIBLE);
//
//            }
//        }
//
//    }
//
//    private void setMonthlyTrendText() {
//        monthDifference = now.getMonthValue() - startDate.getMonthValue();
//
//        if (monthDifference == 1) {
//            if (monthlyTrend.get(3).getMonthExpense() > monthlyTrend.get(3).getBudget()) {
//                setTextOver(3);
//                viewBarChart4.setBackgroundResource(R.drawable.bar_chart_now_high);
//            } else {
//                setTextLess(3);
//                viewBarChart4.setBackgroundResource(R.drawable.bar_chart_now_low);
//            }
//        } else if (monthDifference == 2) {
//            if (monthlyTrend.get(2).getMonthExpense() > monthlyTrend.get(3).getBudget()) {
//                setTextOver(2);
//                viewBarChart3.setBackgroundResource(R.drawable.bar_chart_now_high);
//            } else {
//                setTextLess(2);
//                viewBarChart3.setBackgroundResource(R.drawable.bar_chart_now_low);
//            }
//        } else if (monthDifference == 3) {
//            if (monthlyTrend.get(1).getMonthExpense() > monthlyTrend.get(3).getBudget()) {
//                setTextOver(1);
//                viewBarChart2.setBackgroundResource(R.drawable.bar_chart_now_high);
//            } else {
//                setTextLess(1);
//                viewBarChart2.setBackgroundResource(R.drawable.bar_chart_now_low);
//            }
//        } else if (monthDifference == 4) {
//            if (monthlyTrend.get(0).getMonthExpense() > monthlyTrend.get(3).getBudget()) {
//                setTextOver(0);
//                viewBarChart1.setBackgroundResource(R.drawable.bar_chart_now_high);
//            } else {
//                setTextLess(0);
//                viewBarChart1.setBackgroundResource(R.drawable.bar_chart_now_low);
//            }
//        }
//
//    }
//
//    //초과시
//    private void setTextOver(int position) {
//
//        tvNotice.setText(formatter.format(monthlyTrend.get(position).getMonthExpense() - monthlyTrend.get(position).getBudget()) + "원 초과됐어요");
//
//        tvDifference.setText("총" + formatter.format(monthlyTrend.get(position).getBudget() - monthlyTrend.get(position).getMonthExpense()) + "원 앆ㅆ어요");
//        tvDifference.setTextColor(Color.parseColor("#107D69"));
//
//        tvMonthBudgetBarChart.setText(formatter.format(monthlyTrend.get(position).getBudget()) + "원");
//
//        tvPossibleText.setText("총 지출");
//        tvPossibleAmount.setText(formatter.format(monthlyTrend.get(position).getMonthExpense()) + "원");
//        tvPossibleAmount.setTextColor(Color.parseColor("#212529"));
//
//
//        tvRemainText.setVisibility(View.VISIBLE);
//        tvRemainText.setText("아낀 돈");
//        tvRemainAmount.setVisibility(View.VISIBLE);
//        tvRemainAmount.setText(formatter.format(monthlyTrend.get(position).getBudget() - monthlyTrend.get(position).getMonthExpense()) + "원");
//        tvRemainAmount.setTextColor(Color.parseColor("#107D69"));
//
//    }
//
//    //예산 이내(이전 달)
//    private void setTextLess(int position) {
//
//
//        if (monthlyTrend.get(position).getMonthExpense() < monthlyTrend.get(position).getBudget()) {
//
//            tvDifference.setText(monthlyTrend.get(position).getMonth() + "월 지출 내역");
//            tvDifference.setTextColor(Color.parseColor("#107D69"));
//
//            tvDifference.setText(formatter.format(monthlyTrend.get(position).getMonthExpense() - monthlyTrend.get(position).getBudget()) + "원 초과됐어요");
//            tvDifference.setTextColor(Color.parseColor("#FC3781"));
//
//            tvMonthBudgetBarChart.setText(formatter.format(monthlyTrend.get(position).getBudget()) + "원");
//
//            tvPossibleText.setText("총 지출");
//            tvPossibleAmount.setText(formatter.format(monthlyTrend.get(position).getMonthExpense()) + "원");
//            tvPossibleAmount.setTextColor(Color.parseColor("#212529"));
//
//
//            tvRemainText.setVisibility(View.VISIBLE);
//            tvRemainText.setText("초과된 돈");
//            tvRemainAmount.setVisibility(View.VISIBLE);
//            tvRemainAmount.setText(formatter.format(monthlyTrend.get(position).getMonthExpense() - monthlyTrend.get(position).getBudget()) + "원");
//            tvRemainAmount.setTextColor(Color.parseColor("#FC3781"));
//        }
//    }
//

    private void getDetailServer(LocalDate startDate, LocalDate endDate, int categoryId, boolean isCustom) {

        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMonthlyDetail(startDate, endDate, categoryId, isCustom);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("monthly Trend", responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
//                            JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                            Gson gson = new Gson();
                            expenditureDetailDto = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<ExpenditureDetailDto>() {
                            }.getType());

                        }

                    }

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

    private void setListener(){

        ivDetail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetailServer(startDate, endDate, goal.get(0).getCategoryType().getCategoryId(),goal.get(0).getCategoryType().getCustom());
                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
//                intent.putExtra("detail",expenditureDetailDto.getExpenseDetail());
////                intent.putExtra("isElse",false);
                intent.putExtra("month",startDate.getMonthValue());

                intent.putExtra("category name",goal.get(0).getCategoryName());
                intent.putExtra("percentage", goal.get(0).getPercentage());
                intent.putExtra("expense", goal.get(0).getExpense());
//                intent.putExtra("detail", (ArrayList)goal.get(0).getExpenditureDetailDtoList());
                startActivity(intent);
            }
        });

        ivDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
                intent.putExtra("isElse",false);
                intent.putExtra("category name",goal.get(1).getCategoryName());
                intent.putExtra("percentage", goal.get(1).getPercentage());
                intent.putExtra("expense", goal.get(1).getExpense());
                intent.putExtra("detail", (ArrayList)goal.get(1).getExpenditureDetailDtoList());
                startActivity(intent);
            }
        });

        ivDetail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
                intent.putExtra("isElse",false);
                intent.putExtra("category name",goal.get(2).getCategoryName());
                intent.putExtra("percentage", goal.get(2).getPercentage());
                intent.putExtra("expense", goal.get(2).getExpense());
                intent.putExtra("detail", (ArrayList)goal.get(2).getExpenditureDetailDtoList());
                startActivity(intent);
            }
        });

        ivDetail4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
                intent.putExtra("isElse",true);
                intent.putExtra("category name","나머지");
                intent.putExtra("percentage", 100-percentSum);
                intent.putExtra("expense", responseMonthDetail.getTotalExpense()-expenseSum);
                for(int i=2; i<goal.size();i++){
                    intent.putExtra("detail"+(i-2), (ArrayList)goal.get(i).getExpenditureDetailDtoList());
                }
                startActivity(intent);
            }
        });

    }

    private void showDetail() {
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MonthlyDetailActivity.class);
//                startActivity(intent);
//
//            }
//        };
//        ivDetail1.setOnClickListener(onClickListener);
    }
}

