package com.dnd.moneyroutine.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.ExpenditureCategoryAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.YearMonthPickerDialog;
import com.dnd.moneyroutine.dto.CategoryType;
import com.dnd.moneyroutine.dto.ExpenditureDetail;
import com.dnd.moneyroutine.dto.GoalCategoryInfo;
import com.dnd.moneyroutine.dto.MonthlyTrend;
import com.dnd.moneyroutine.dto.ExpenditureStatistics;
import com.dnd.moneyroutine.dto.WeeklyTrend;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.LocalDateSerializer;
import com.dnd.moneyroutine.service.RetrofitService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ExpenditureMonthlyFragment extends Fragment {

    private static final String TAG = "ExpenditureMonthly";

    private TextView tvEmpty;
    private LinearLayout llExpenditure;

    private LinearLayout llSelectMonth;
    private TextView tvMonth;
    private TextView tvDate;

    private PieChart pieChart;

    private TextView tvTopCategory;
    private TextView tvTopRatio;
    private TextView tvTotalExpenditure;

    private RecyclerView rvCategory;

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

    private String token;

    private ExpenditureStatistics responseMonthStatistics;
    private ArrayList<GoalCategoryInfo> goalCategoryInfoList;
    private DecimalFormat decimalFormat;

    private ExpenditureDetail expenditureDetailDto;

    private List<MonthlyTrend> monthlyTrend;

    private LocalDate nowDate;
    private LocalDate startDate;
    private LocalDate endDate;

    public ExpenditureMonthlyFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure_monthly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initField();

        setMonthDate();
        setListener();

        getMonthlyStatistics(startDate, endDate);
        getMonthlyTrend(nowDate);
    }

    private void initView(View v) {
        tvEmpty = v.findViewById(R.id.tv_monthly_empty);
        llExpenditure = v.findViewById(R.id.ll_montly_expenditure_content);

        llSelectMonth = v.findViewById(R.id.ll_month_select);
        tvMonth = v.findViewById(R.id.tv_expenditure_month);
        tvDate = v.findViewById(R.id.tv_start_end_month);

        pieChart = v.findViewById(R.id.pie_chart_month);

        tvTopCategory = v.findViewById(R.id.tv_month_top_category);
        tvTopRatio = v.findViewById(R.id.tv_month_top_percent);
        tvTotalExpenditure = v.findViewById(R.id.tv_total_month);

        rvCategory = v.findViewById(R.id.rv_month_category);

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
    }

    private void initField() {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);
        nowDate = LocalDate.now();
        decimalFormat = new DecimalFormat("#,###");
    }

    private void setMonthDate() {
        startDate = nowDate.withDayOfMonth(1);
        endDate = nowDate.withDayOfMonth(nowDate.lengthOfMonth());

        String start = startDate.format(DateTimeFormatter.ofPattern("M.d"));
        String end = endDate.format(DateTimeFormatter.ofPattern("M.d"));
        tvDate.setText(start + " ~ " + end);

        tvMonth.setText(startDate.format(DateTimeFormatter.ofPattern("y. M월")));

//        startDate = YearMonth.now().atDay(1);
//        endDate = YearMonth.now().atEndOfMonth();
    }

    private void setListener(){
        llSelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 연도, 월 선택 다이얼로그 띄우기
                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(nowDate, false);
                yearMonthPickerDialog.show(getActivity().getSupportFragmentManager(), "YearMonthPickerDialog");

                yearMonthPickerDialog.setOnSelectListener(new YearMonthPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(LocalDate date) {
                        nowDate = date;

                        setMonthDate();
                        getMonthlyStatistics(startDate, endDate);
                        getMonthlyTrend(startDate);
                    }
                });
            }
        });
    }

    // 월별 소비 내역 가져오기
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

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
                            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
                            responseMonthStatistics = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<ExpenditureStatistics>() {}.getType());

                            if (responseMonthStatistics.getGoalCategoryInfoList().isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                llExpenditure.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                llExpenditure.setVisibility(View.VISIBLE);

                                setEtcList();
                                drawPieChart();
                                setContent();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEtcList() {
        goalCategoryInfoList = (ArrayList<GoalCategoryInfo>) responseMonthStatistics.getGoalCategoryInfoList();

        ArrayList<CategoryType> etcCategoryTypes = new ArrayList<>();
        ArrayList<String> etcCategoryNames = new ArrayList<>();

        int etcPercent = 0;
        int etcExpense = 0;

        for (int i = 3; i < goalCategoryInfoList.size(); i++) {
            GoalCategoryInfo info = goalCategoryInfoList.get(i);

            etcPercent += info.getPercentage();
            etcExpense += info.getExpense();

            if (!etcCategoryNames.contains(info.getCategoryName())) {
                etcCategoryNames.add(info.getCategoryName());
                etcCategoryTypes.add(info.getCategoryType());
            }
        }

        if (etcExpense > 0) {
            GoalCategoryInfo info = goalCategoryInfoList.get(3);

            info.setCategoryName("나머지");
            info.setPercentage(etcPercent);
            info.setExpense(etcExpense);

            info.setEtcCategoryNames(etcCategoryNames);
            info.setEtcCategoryTypes(etcCategoryTypes);

            for (int i = 4; i < goalCategoryInfoList.size(); i++) {
                goalCategoryInfoList.remove(i--);
            }
        }
    }

    // 카테고리 파이 차트 그리기
    private void drawPieChart() {
        pieChart.setVisibility(View.VISIBLE);
        pieChart.removeAllViews();

//        pieChart.setRotationAngle(-100); //시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); //차트 설명 제거
        pieChart.getLegend().setEnabled(false); //아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0, 0, 0, 0); //차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); //가운데 hole
        pieChart.setHoleRadius(55f); //hole 크기 설정
        pieChart.setTransparentCircleRadius(0);

        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            try {
                values.add(new PieEntry(goalCategoryInfoList.get(i).getPercentage()));
            } catch ( IndexOutOfBoundsException e ) {
                values.add(new PieEntry(0));
            }
        }

        PieDataSet dataSet = new PieDataSet(values, "총 지출");

        //차트 색상 설정
        List<Integer> colors = getColorArray();
        dataSet.setColors(colors);

        pieChart.setDrawMarkers(false); //차트 색상 사이 간격
        pieChart.setDrawEntryLabels(false); //차트위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pieChart.setData(data);
    }

    private void setContent() {
        tvTotalExpenditure.setText(decimalFormat.format(responseMonthStatistics.getTotalExpense()) + "원");
        tvTopCategory.setText(goalCategoryInfoList.get(0).getCategoryName());
        tvTopRatio.setText(goalCategoryInfoList.get(0).getPercentage() + "%");

        ExpenditureCategoryAdapter expenditureCategoryAdapter = new ExpenditureCategoryAdapter(goalCategoryInfoList, false, false, startDate, endDate);
        rvCategory.setAdapter(expenditureCategoryAdapter);
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // 월별 소비 동향 가져오기 : 막대 그래프에 사용
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

                    Log.d(TAG, responseJson.toString());

                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
                    ArrayList<MonthlyTrend> monthlyTrends = gson.fromJson(responseJson.getAsJsonObject("data").getAsJsonArray("monthExpenseInfoDtoList"),
                            new TypeToken<ArrayList<MonthlyTrend>>() {}.getType());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 그래프 컬러 배열 반환하는 메소드
    private List<Integer> getColorArray() {
        String[] colorStringArray = getResources().getStringArray(R.array.category_color);;

        List<Integer> colorIntList = new ArrayList<>();

        for(String color : colorStringArray) {
            colorIntList.add(Color.parseColor(color));
        }

        return colorIntList;
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

}

