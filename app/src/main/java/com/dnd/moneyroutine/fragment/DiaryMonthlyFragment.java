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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.ExpenditureCategoryAdapter;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.EmotionDialog;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.YearMonthPickerDialog;
import com.dnd.moneyroutine.dto.ExpenditureCompact;
import com.dnd.moneyroutine.dto.MonthlyDiary;
import com.dnd.moneyroutine.enums.EmotionEnum;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.LocalDateSerializer;
import com.dnd.moneyroutine.service.RetrofitService;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DiaryMonthlyFragment extends Fragment {

    private static final String TAG = "DiaryMonthly";

    private ConstraintLayout clEmotionChart;
    private ConstraintLayout clCategoryChart;
    private ConstraintLayout clBestCategory;

    private LinearLayout btnSelectYearMonth;
    private TextView tvSelectYearMonth;

    private TextView tvBestEmotionTitle;

    private HorizontalBarChart hcEmotion;
    private TextView tvGoodCnt;
    private TextView tvSosoCnt;
    private TextView tvBadCnt;

    private LinearLayout btnSelectEmotion;
    private ImageView ivSelectEmotion;
    private TextView tvSelectEmotion;
    private TextView tvSelectEmotionDetail;

    private TextView tvBestEmotionCatTitle;
    private TextView tvBestCat;
    private TextView tvBestCatCnt;

    private PieChart pcCategory;

    private TextView tvSelectEmotionTitle;
    private TextView tvSelectEmotionCnt;

    private RecyclerView rvCategory;

    private String token;
    private LocalDate selectDate;
    private EmotionEnum selectEmotion;
    private HashMap<EmotionEnum, Integer> emotionMap;
    private ArrayList<MonthlyDiary> monthlyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_monthly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initField();
        setListener();

        getMonthlyEmotionList();
    }

    private void initView(View v) {
        clEmotionChart = v.findViewById(R.id.cl_emotion_chart);
        clCategoryChart = v.findViewById(R.id.cl_month_pie_chart);
        clBestCategory = v.findViewById(R.id.cl_best_category);

        btnSelectYearMonth = v.findViewById(R.id.ll_diary_select_month);
        tvSelectYearMonth = v.findViewById(R.id.tv_diary_select_month);

        tvBestEmotionTitle = v.findViewById(R.id.tv_month_best_emotion_title);

        hcEmotion = v.findViewById(R.id.bar_month_emotion);
        tvGoodCnt = v.findViewById(R.id.tv_month_good_cnt);
        tvSosoCnt = v.findViewById(R.id.tv_month_soso_cnt);
        tvBadCnt = v.findViewById(R.id.tv_month_bad_cnt);

        btnSelectEmotion = v.findViewById(R.id.ll_month_select_emotion);
        ivSelectEmotion = v.findViewById(R.id.iv_month_select_emotion);
        tvSelectEmotion = v.findViewById(R.id.tv_month_select_emotion);
        tvSelectEmotionDetail = v.findViewById(R.id.tv_month_select_emotion_detail);

        tvBestEmotionCatTitle = v.findViewById(R.id.tv_month_best_cat_emotion_title);
        tvBestCat = v.findViewById(R.id.tv_month_best_category);
        tvBestCatCnt = v.findViewById(R.id.tv_month_best_cat_cnt);
        pcCategory = v.findViewById(R.id.pie_chart_month);
        tvSelectEmotionTitle = v.findViewById(R.id.tv_month_select_emotion_title);
        tvSelectEmotionCnt = v.findViewById(R.id.tv_month_select_emotion_cnt);

        rvCategory = v.findViewById(R.id.rv_month_category);
    }

    private void initField() {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);

        selectDate = LocalDate.now();
        tvSelectYearMonth.setText(Common.getMainLocalDateToString(selectDate) + " 소비 감정");
    }

    private void setListener() {
        btnSelectYearMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 연도, 월 선택 다이얼로그 띄우기
                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(selectDate, false);
                yearMonthPickerDialog.show(getActivity().getSupportFragmentManager(), "YearMonthPickerDialog");

                yearMonthPickerDialog.setOnSelectListener(new YearMonthPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(LocalDate date) {
                        selectDate = date;
                        tvSelectYearMonth.setText(Common.getMainLocalDateToString(date) + " 소비 감정");

                        getMonthlyEmotionList();
                    }
                });
            }
        });

        btnSelectEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 소비 감정 선택 다이얼로그 띄우기
                EmotionDialog emotionDialog = new EmotionDialog(selectEmotion);
                emotionDialog.show(getActivity().getSupportFragmentManager(), "EmotionPickerDialog");

                emotionDialog.setOnSelectListener(new EmotionDialog.OnSelectListener() {
                    @Override
                    public void onSelect(EmotionEnum emotion) {
                        selectEmotion = emotion;

                        setSelectEmotion();
                        getMonthlyDiaryByEmotion();
                    }
                });
            }
        });
    }

    // 소비 감정 횟수 가져오기
    private void getMonthlyEmotionList() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMonthlyBestEmotion(selectDate.getYear(), selectDate.getMonthValue());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        Gson gson = new Gson();
                        emotionMap = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<HashMap<EmotionEnum, Integer>>() {}.getType());

                        if (emotionMap.size() == 0) {
                            tvBestEmotionTitle.setText("소비 내역이 없어요");

                            clEmotionChart.setVisibility(View.GONE);
                            clCategoryChart.setVisibility(View.GONE);
                            rvCategory.setVisibility(View.GONE);
                        } else {
                            clEmotionChart.setVisibility(View.VISIBLE);
                            clCategoryChart.setVisibility(View.VISIBLE);
                            rvCategory.setVisibility(View.VISIBLE);

                            emotionMap.putIfAbsent(EmotionEnum.GOOD, 0);
                            emotionMap.putIfAbsent(EmotionEnum.SOSO, 0);
                            emotionMap.putIfAbsent(EmotionEnum.BAD, 0);

                            setEmotionInfo();
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

    private void setEmotionInfo() {
        // 가장 큰 값을 가지는 소비 감정 찾기
        Comparator<Map.Entry<EmotionEnum, Integer>> comparator = new Comparator<Map.Entry<EmotionEnum, Integer>>() {
            @Override
            public int compare(Map.Entry<EmotionEnum, Integer> e1, Map.Entry<EmotionEnum, Integer> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        };

        // Max Value의 key, value
        Map.Entry<EmotionEnum, Integer> maxEntry = Collections.max(emotionMap.entrySet(), comparator);
        switch (maxEntry.getKey()) {
            case GOOD:
                tvBestEmotionTitle.setText("'만족'한 소비가 가장 많아요!");
                break;
            case SOSO:
                tvBestEmotionTitle.setText("'보통' 소비가 가장 많아요");
                break;
            case BAD:
                tvBestEmotionTitle.setText("이번 달 소비를 조금 더 살펴볼까요?");
                break;
        }

        tvGoodCnt.setText(emotionMap.get(EmotionEnum.GOOD) + "번");
        tvSosoCnt.setText(emotionMap.get(EmotionEnum.SOSO) + "번");
        tvBadCnt.setText(emotionMap.get(EmotionEnum.BAD) + "번");

        selectEmotion = maxEntry.getKey();

        drawEmotionBarChart(); // 소비 감정 그래프 그리기
        setSelectEmotion();
        getMonthlyDiaryByEmotion(); // 가장 많은 소비 감정으로 지출 내역 먼저 가져오기
    }

    // 소비 감정 그래프 그리기 : 막대그래프
    private void drawEmotionBarChart() {
        hcEmotion.setVisibility(View.VISIBLE);
        hcEmotion.removeAllViews();

        List<Integer> colorArray = getColorArray(true);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, new float[] {emotionMap.get(EmotionEnum.GOOD), emotionMap.get(EmotionEnum.SOSO), emotionMap.get(EmotionEnum.BAD)}));

        BarDataSet barDataSet = new BarDataSet(barEntries, "emotionChart");
        barDataSet.setColors(colorArray);

        hcEmotion.setData(new BarData(barDataSet));
        hcEmotion.setTouchEnabled(false); // 그래프 터치 X

        // 그래프 x, y축 표시 X
        hcEmotion.getXAxis().setEnabled(false);
        hcEmotion.getAxisLeft().setEnabled(false);
        hcEmotion.getAxisRight().setEnabled(false);

        hcEmotion.getData().setDrawValues(false); // 그래프 수치 표시 X
        hcEmotion.getDescription().setEnabled(false); // 그래프 하단 설명 사용 X
        hcEmotion.getLegend().setEnabled(false); // 그래프 왼쪽 하단 설명 사용 X
    }

    // 선택한 소비 감정 정보 바인딩
    private void setSelectEmotion() {
        tvSelectEmotion.setText(selectEmotion.getDetail());
        tvSelectEmotionTitle.setText(selectEmotion.getDetail() + " 소비 횟수");
        tvSelectEmotionCnt.setText(emotionMap.get(selectEmotion) + "번");

        switch (selectEmotion) {
            case GOOD:
                ivSelectEmotion.setImageResource(R.drawable.icon_good);
                tvSelectEmotionDetail.setText("행복했어");
                tvSelectEmotionDetail.setTextColor(Color.parseColor("#107D69"));
                break;
            case SOSO:
                ivSelectEmotion.setImageResource(R.drawable.icon_soso);
                tvSelectEmotionDetail.setText("그냥 그랬어");
                tvSelectEmotionDetail.setTextColor(Color.parseColor("#1E5CA4"));
                break;
            case BAD:
                ivSelectEmotion.setImageResource(R.drawable.icon_bad);
                tvSelectEmotionDetail.setText("왜썼지");
                tvSelectEmotionDetail.setTextColor(Color.parseColor("#D13474"));
                break;
        }
    }

    // 소비 감정으로 지출 내역 가져오기
    private void getMonthlyDiaryByEmotion() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMonthlyDiaryByEmotion(selectDate.getYear(), selectDate.getMonthValue(), selectEmotion);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
                        monthlyList = gson.fromJson(jsonArray, new TypeToken<ArrayList<MonthlyDiary>>() {}.getType());

                        if (monthlyList.size() == 0) {
                            clBestCategory.setVisibility(View.GONE);
                            pcCategory.setVisibility(View.GONE);
                            rvCategory.setVisibility(View.GONE);

                            tvBestEmotionCatTitle.setText("소비 내역이 없어요");
                        } else {
                            clBestCategory.setVisibility(View.VISIBLE);
                            pcCategory.setVisibility(View.VISIBLE);
                            rvCategory.setVisibility(View.VISIBLE);

                            setEtcList();
                            drawCategoryPieChart();
                            setBestCategoryInfo();
                            setExpenditureInfo();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 상위 3개 카테고리와 나머지 카테고리 분류
    private void setEtcList() {
        ArrayList<ExpenditureCompact> etcList = new ArrayList<>();
        int etcCnt = 0;

        for (int i = 0; i < monthlyList.size(); i++) {
            MonthlyDiary monthlyDiary = monthlyList.get(i);

            if (i >= 3) {
                etcCnt += monthlyDiary.getCount();
                etcList.addAll(monthlyDiary.getExpenditureList());
            }
        }

        if (etcCnt > 0) {
            MonthlyDiary monthlyDiary = monthlyList.get(3);
            monthlyDiary.setName("나머지");
            monthlyDiary.setCount(etcCnt);
            monthlyDiary.setExpenditureList(etcList);

            for (int i = 4; i < monthlyList.size(); i++) {
                monthlyList.remove(i--);
            }
        }
    }

    // 카테고리 소비 그래프 그리기 : 파이 차트
    private void drawCategoryPieChart() {
        pcCategory.setVisibility(View.VISIBLE);
        pcCategory.removeAllViews();

//        pcCategory.setRotationAngle(100); // 시작 위치 설정 (3시방향이 기본)
        pcCategory.getDescription().setEnabled(false); // 차트 설명 제거
        pcCategory.getLegend().setEnabled(false); // 아래 색깔별 항목 설명 제거

        pcCategory.setExtraOffsets(0, 0, 0, 0); // 차트 주변 margin 설정
        pcCategory.setTouchEnabled(false); // 터치 애니메이션 설정
        pcCategory.setDrawHoleEnabled(true); // 가운데 hole
        pcCategory.setHoleRadius(55f); // hole 크기 설정
        pcCategory.setTransparentCircleRadius(0);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (int i = 3; i >= 0; i--) {
            try {
                yValues.add(new PieEntry(monthlyList.get(i).getCount()));
            } catch ( IndexOutOfBoundsException e ) {
                yValues.add(new PieEntry(0));
            }
        }

        PieDataSet dataSet = new PieDataSet(yValues, "Countries");

        // 차트 색상 설정
        dataSet.setColors(getColorArray(false));

        pcCategory.setDrawMarkers(false); // 차트 색상 사이 간격
        pcCategory.setDrawEntryLabels(false); // 차트 위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pcCategory.setData(data);
    }

    // 가장 소비가 많았던 카테고리 정보 바인딩
    private void setBestCategoryInfo() {
        MonthlyDiary best = monthlyList.get(0); // 첫 번째에 있는 것이 가장 소비가 많은 카테고리

        switch (selectEmotion) {
            case GOOD:
                tvBestEmotionCatTitle.setText(best.getName() + "에서 가장 만족을 느꼈어요!");
                break;
            case SOSO:
                tvBestEmotionCatTitle.setText(best.getName() + " 관련 소비가 그냥 그랬어요");
                break;
            case BAD:
                tvBestEmotionCatTitle.setText(best.getName() + " 관련 소비를 체크해볼까요?");
                break;
        }

        tvBestCat.setText(best.getName());
        tvBestCatCnt.setText(best.getCount() + "번");
    }

    // 세부 소비 내역 정보 바인딩
    private void setExpenditureInfo() {
        ExpenditureCategoryAdapter monthlyCategoryAdapter = new ExpenditureCategoryAdapter(monthlyList, true);
        rvCategory.setAdapter(monthlyCategoryAdapter);
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // 그래프 컬러 배열 반환하는 메소드
    private List<Integer> getColorArray(boolean emotion) {
        String[] colorStringArray;

        if (emotion) {
            colorStringArray = getResources().getStringArray(R.array.emotion_color);
        } else {
            colorStringArray = getResources().getStringArray(R.array.category_reverse_color);
        }

        List<Integer> colorIntList = new ArrayList<>();

        for(String color : colorStringArray) {
            colorIntList.add(Color.parseColor(color));
        }

        return colorIntList;
    }
}
