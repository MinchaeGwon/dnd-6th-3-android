package com.dnd.moneyroutine.fragment;

import androidx.appcompat.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.ExpenditureCategoryAdapter;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.WeekPickerDialog;
import com.dnd.moneyroutine.dto.ExpenditureDetail;
import com.dnd.moneyroutine.dto.GoalCategoryInfo;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ExpenditureWeeklyFragment extends Fragment {

    private TextView tvEmpty;
    private LinearLayout llExpenditure;

    private LinearLayout llSelectWeek;
    private TextView tvWeekNum;
    private TextView tvShowWeekDate;

    private TextView tvTopCategory;
    private TextView tvTopRatio;
    private TextView tvTotalExpenditure;

    private PieChart pieChart;
    private RecyclerView rvCategory;

    private TextView tvHistoryWeek;

    private ConstraintLayout c1Chart1;
    private ConstraintLayout c1Chart2;
    private ConstraintLayout c1Chart3;
    private ConstraintLayout c1Chart4;
    private ConstraintLayout c1Chart5;

    private LinearLayout llInfo;

    private View chart1;
    private View chart2;
    private View chart3;
    private View chart4;
    private View chart5;

    private AlertDialog infoDialog;

    private String token;

    private Calendar selectDate;
    private LocalDate nowDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private DecimalFormat decimalFormat;

    private ExpenditureStatistics responseStatistics;
    private ArrayList<GoalCategoryInfo> goalCategoryInfoList;

    public ExpenditureWeeklyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure_weekly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initField();

        setWeekDate();
        setBtnListener();

        getWeeklyStatistics(startDate, endDate);
        getWeeklyTrend(nowDate);
    }

    private void initView(View v) {
        tvEmpty = v.findViewById(R.id.tv_weekly_empty);
        llExpenditure = v.findViewById(R.id.ll_weekly_expenditure_content);

        llSelectWeek = v.findViewById(R.id.ll_ex_select_week);
        tvWeekNum = v.findViewById(R.id.tv_week_num);
        tvShowWeekDate = v.findViewById(R.id.tv_start_end_week);

        tvTopCategory = v.findViewById(R.id.tv_top_category_week);
        tvTopRatio = v.findViewById(R.id.tv_top_ratio_week);
        tvTotalExpenditure = v.findViewById(R.id.tv_total_week);

        pieChart = v.findViewById(R.id.pie_chart_week);
        rvCategory = v.findViewById(R.id.rv_ex_week_category);

        tvHistoryWeek = v.findViewById(R.id.tv_nth_history);

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

        llInfo = v.findViewById(R.id.ll_week_recommend);
    }

    private void initField() {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);

        selectDate = Calendar.getInstance();
        nowDate = LocalDate.now();

        decimalFormat = new DecimalFormat("#,###");
    }

    // 주, 날짜 설정
    private void setWeekDate() {
        tvWeekNum.setText(Common.getExpenditureWeekString(selectDate));
        tvHistoryWeek.setText(Common.getExpenditureWeekString(selectDate) + " 지출 내역");

        switch (nowDate.getDayOfWeek()) {
            case SUNDAY:
                startDate = nowDate;
                break;
            case MONDAY:
                startDate = nowDate.minusDays(1);
                break;
            case TUESDAY:
                startDate = nowDate.minusDays(2);
                break;
            case WEDNESDAY:
                startDate = nowDate.minusDays(3);
                break;
            case THURSDAY:
                startDate = nowDate.minusDays(4);
                break;
            case FRIDAY:
                startDate = nowDate.minusDays(5);
                break;
            case SATURDAY:
                startDate = nowDate.minusDays(6);
                break;
        }

        endDate = startDate.plusDays(6);

        //textview에 설정
        tvShowWeekDate.setText(startDate.getMonthValue() + "." + startDate.getDayOfMonth() + " ~ " + endDate.getMonthValue() + "." + endDate.getDayOfMonth());
    }

    private void setBtnListener() {
        // 연도, 월, 주 선택 다이얼로그 띄우기
        llSelectWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeekPickerDialog weekPickerDialog = new WeekPickerDialog(selectDate);
                weekPickerDialog.show(getActivity().getSupportFragmentManager(), "WeekPickerDialog");

                weekPickerDialog.setOnSelectListener(new WeekPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(Calendar calendar) {
                        selectDate = calendar;
                        nowDate = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalDate();

                        setWeekDate();
                        getWeeklyStatistics(startDate, endDate);
                        getWeeklyTrend(startDate);
                    }
                });
            }
        });

        // 주별 권장 지출액 다이얼로그 띄우기
        llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoDialog();
                infoDialog.show();
            }
        });
    }

    private void setInfoDialog() {
        if (infoDialog != null) {
            return;
        }

        makeInfoDialog();
    }

    // 주별 권장 지출액 다이얼로그 만들기
    private void makeInfoDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_infomation, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setView(view);
        infoDialog = builder.create();

        Button btnConfirm = view.findViewById(R.id.btn_info_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
            }
        });
    }

    // 주별 소비 내역 가져오기
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

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
                            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
                            responseStatistics = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<ExpenditureStatistics>() {}.getType());

                            if (responseStatistics.getGoalCategoryInfoList().isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                llExpenditure.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                llExpenditure.setVisibility(View.VISIBLE);

                                setEtcList();
                                drawCategoryPieChart();
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
        goalCategoryInfoList = (ArrayList<GoalCategoryInfo>) responseStatistics.getGoalCategoryInfoList();

        ArrayList<ExpenditureDetail> etcList = new ArrayList<>();
        int etcPercent = 0;
        int etcExpense = 0;

        for (int i = 3; i < goalCategoryInfoList.size(); i++) {
            GoalCategoryInfo info = goalCategoryInfoList.get(i);

            etcPercent += info.getPercentage();
            etcExpense += info.getExpense();

            for (ExpenditureDetail expenditure : info.getExpenditureList()) {
                expenditure.setCategoryName(info.getCategoryName());
            }

            etcList.addAll(info.getExpenditureList());
        }

        if (etcExpense > 0) {
            GoalCategoryInfo info = goalCategoryInfoList.get(3);

            info.setCategoryName("나머지");
            info.setPercentage(etcPercent);
            info.setExpense(etcExpense);
            info.setExpenditureList(etcList);

            for (int i = 4; i < goalCategoryInfoList.size(); i++) {
                goalCategoryInfoList.remove(i--);
            }
        }
    }

    // 카테고리 파이차트 그리기
    private void drawCategoryPieChart() {
        pieChart.setVisibility(View.VISIBLE);
        pieChart.removeAllViews();

//        pieChart.setRotationAngle(-100); // 시작 위치 설정 (3시방향이 기본)
        pieChart.getDescription().setEnabled(false); // 차트 설명 제거
        pieChart.getLegend().setEnabled(false); // 아래 색깔별 항목 설명 제거

        pieChart.setExtraOffsets(0, 0, 0, 0); // 차트 주변 margin 설정
        pieChart.setTouchEnabled(false); // 터치 애니메이션 설정
        pieChart.setDrawHoleEnabled(true); // 가운데 hole
        pieChart.setHoleRadius(55f); // hole 크기 설정
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

        // 차트 색상 설정
        List<Integer> colors = getColorArray();
        dataSet.setColors(colors);

        pieChart.setDrawMarkers(false); // 차트 색상 사이 간격
        pieChart.setDrawEntryLabels(false); // 차트 위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pieChart.setData(data);
    }

    private void setContent() {
        tvTotalExpenditure.setText(decimalFormat.format(responseStatistics.getTotalExpense()) + "원");
        tvTopCategory.setText(goalCategoryInfoList.get(0).getCategoryName());
        tvTopRatio.setText(goalCategoryInfoList.get(0).getPercentage() + "%");

        ExpenditureCategoryAdapter expenditureCategoryAdapter = new ExpenditureCategoryAdapter(goalCategoryInfoList, false, true);
        rvCategory.setAdapter(expenditureCategoryAdapter);
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // 주별 소비 동향 가져오기 : 그래프에 사용
    private void getWeeklyTrend(LocalDate currentDate){
        Log.d("trend", currentDate.toString());

        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getWeeklyTrend(currentDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("trend", responseJson.toString());

                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();

                    ArrayList<WeeklyTrend> weeklyTrends = gson.fromJson(responseJson.getAsJsonObject("data").getAsJsonArray("weekExpenseInfoDtoList"),
                            new TypeToken<ArrayList<WeeklyTrend>>() {}.getType());
                } else {
                    Log.e("trend", "22 error: " + response.code());

                    return;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("week", "22 failure : "+ t.getMessage());
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

}