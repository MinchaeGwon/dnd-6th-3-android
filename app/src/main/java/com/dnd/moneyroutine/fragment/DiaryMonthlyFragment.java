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
import com.dnd.moneyroutine.adapter.MonthlyDiaryAdapter;
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
    private LinearLayout llCategoryList;
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

    private LinearLayout btnFirstCat;
    private TextView tvFirstCat;
    private TextView tvFirstCnt;
    private TextView tvFirstMore;
    private ImageView ivFirstMore;
    private ImageView ivFirstHold;
    private RecyclerView rvFirstCat;

    private LinearLayout btnSecondCat;
    private TextView tvSecondCat;
    private TextView tvSecondCnt;
    private TextView tvSecondMore;
    private ImageView ivSecondMore;
    private ImageView ivSecondHold;
    private RecyclerView rvSecondCat;

    private LinearLayout btnThirdCat;
    private TextView tvThirdCat;
    private TextView tvThirdCnt;
    private TextView tvThirdMore;
    private ImageView ivThirdMore;
    private ImageView ivThirdHold;
    private RecyclerView rvThirdCat;

    private LinearLayout btnEtcCat;
    private TextView tvEtcCnt;
    private TextView tvEtcMore;
    private ImageView ivEtcMore;
    private ImageView ivEtcHold;
    private RecyclerView rvEtcCat;

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
        llCategoryList = v.findViewById(R.id.ll_category);
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

        btnFirstCat = v.findViewById(R.id.ll_month_first);
        tvFirstCat = v.findViewById(R.id.tv_month_first_cat);
        tvFirstCnt = v.findViewById(R.id.tv_month_first_cnt);
        tvFirstMore = v.findViewById(R.id.tv_month_more_first);
        ivFirstMore = v.findViewById(R.id.iv_month_first_more);
        ivFirstHold = v.findViewById(R.id.iv_month_first_hold);
        rvFirstCat = v.findViewById(R.id.rv_month_first);

        btnSecondCat = v.findViewById(R.id.ll_month_second);
        tvSecondCat = v.findViewById(R.id.tv_month_second_cat);
        tvSecondCnt = v.findViewById(R.id.tv_month_second_cnt);
        tvSecondMore = v.findViewById(R.id.tv_month_more_second);
        ivSecondMore = v.findViewById(R.id.iv_month_second_more);
        ivSecondHold = v.findViewById(R.id.iv_month_second_hold);
        rvSecondCat = v.findViewById(R.id.rv_month_second);

        btnThirdCat = v.findViewById(R.id.ll_month_third);
        tvThirdCat = v.findViewById(R.id.tv_month_third_cat);
        tvThirdCnt = v.findViewById(R.id.tv_month_third_cnt);
        tvThirdMore = v.findViewById(R.id.tv_month_more_third);
        ivThirdMore = v.findViewById(R.id.iv_month_third_more);
        ivThirdHold = v.findViewById(R.id.iv_month_third_hold);
        rvThirdCat = v.findViewById(R.id.rv_month_third);

        btnEtcCat = v.findViewById(R.id.ll_month_etc);
        tvEtcCnt = v.findViewById(R.id.tv_month_etc_cnt);
        tvEtcMore = v.findViewById(R.id.tv_month_more_etc);
        ivEtcMore = v.findViewById(R.id.iv_month_etc_more);
        ivEtcHold = v.findViewById(R.id.iv_month_etc_hold);
        rvEtcCat = v.findViewById(R.id.rv_month_etc);
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
                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(selectDate);
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

                        setGoneRecycler(false, rvFirstCat, ivFirstMore, ivFirstHold, tvFirstMore);
                        setGoneRecycler(false, rvSecondCat, ivSecondMore, ivSecondHold, tvSecondMore);
                        setGoneRecycler(false, rvThirdCat, ivThirdMore, ivThirdHold, tvThirdMore);
                        setGoneRecycler(false, rvEtcCat, ivEtcMore, ivEtcHold, tvEtcMore);

                        setSelectEmotion();
                        getMonthlyDiaryByEmotion();
                    }
                });
            }
        });

        btnFirstCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoneRecycler(rvFirstCat.getVisibility() == View.GONE, rvFirstCat, ivFirstMore, ivFirstHold, tvFirstMore);
            }
        });

        btnSecondCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoneRecycler(rvSecondCat.getVisibility() == View.GONE, rvSecondCat, ivSecondMore, ivSecondHold, tvSecondMore);
            }
        });

        btnThirdCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoneRecycler(rvThirdCat.getVisibility() == View.GONE, rvThirdCat, ivThirdMore, ivThirdHold, tvThirdMore);
            }
        });

        btnEtcCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoneRecycler(rvEtcCat.getVisibility() == View.GONE, rvEtcCat, ivEtcMore, ivEtcHold, tvEtcMore);
            }
        });
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
                        emotionMap = gson.fromJson(responseJson.getAsJsonObject("data"),
                                new TypeToken<HashMap<EmotionEnum, Integer>>() {}.getType());

                        if (emotionMap.size() == 0) {
                            tvBestEmotionTitle.setText("소비 내역이 없어요");

                            clEmotionChart.setVisibility(View.GONE);
                            clCategoryChart.setVisibility(View.GONE);
                            llCategoryList.setVisibility(View.GONE);
                        } else {
                            clEmotionChart.setVisibility(View.VISIBLE);
                            clCategoryChart.setVisibility(View.VISIBLE);
                            llCategoryList.setVisibility(View.VISIBLE);

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
        drawEmotionChart();

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

        setSelectEmotion();
        getMonthlyDiaryByEmotion(); // 가장 많은 소비 감정으로 지출 내역 먼저 가져오기
    }

    // 소비 감정 그래프 그리기
    private void drawEmotionChart() {
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
                            llCategoryList.setVisibility(View.GONE);

                            tvBestEmotionCatTitle.setText("소비 내역이 없어요");
                        } else {
                            clBestCategory.setVisibility(View.VISIBLE);
                            pcCategory.setVisibility(View.VISIBLE);
                            llCategoryList.setVisibility(View.VISIBLE);

                            drawCategoryChart();
                            setCategoryInfo();
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

    // 카테고리 파이 차트 그리기
    private void drawCategoryChart() {
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

        ArrayList<PieEntry> yValues = new ArrayList();

        int etcCnt = 0;
        if (monthlyList.size() > 3) {
            for (int i = 3; i < monthlyList.size(); i++) {
                etcCnt += monthlyList.get(i).getCount();
            }
        }

        yValues.add(new PieEntry(etcCnt, "나머지"));
        yValues.add(new PieEntry(monthlyList.size() > 2 ? monthlyList.get(2).getCount() : 0));
        yValues.add(new PieEntry(monthlyList.size() > 1 ? monthlyList.get(1).getCount() : 0));
        yValues.add(new PieEntry(monthlyList.size() > 0 ? monthlyList.get(0).getCount() : 0));

        PieDataSet dataSet = new PieDataSet(yValues, "Countries");

        // 차트 색상 설정
        dataSet.setColors(getColorArray(false));

        pcCategory.setDrawMarkers(false); // 차트 색상 사이 간격
        pcCategory.setDrawEntryLabels(false); // 차트 위 설명 제거

        PieData data = new PieData((dataSet));
        dataSet.setDrawValues(false);

        pcCategory.setData(data);
    }

    private void setCategoryInfo() {
        String category = null;
        int cnt = 0;

        for (MonthlyDiary monthlyDiary : monthlyList) {
            if (monthlyDiary.getCount() > cnt) {
                cnt = monthlyDiary.getCount();
                category = monthlyDiary.getName();
            }
        }

        switch (selectEmotion) {
            case GOOD:
                tvBestEmotionCatTitle.setText(category + "에서 가장 만족을 느꼈어요!");
                break;
            case SOSO:
                tvBestEmotionCatTitle.setText(category + " 관련 소비가 그냥 그랬어요");
                break;
            case BAD:
                tvBestEmotionCatTitle.setText(category + "관련 소비를 체크해볼까요?");
                break;
        }

        tvBestCat.setText(category);
        tvBestCatCnt.setText(cnt + "번");
    }

    private void setExpenditureInfo() {
        ArrayList<ExpenditureCompact> firstList = new ArrayList<>();
        ArrayList<ExpenditureCompact> secondList = new ArrayList<>();
        ArrayList<ExpenditureCompact> thirdList = new ArrayList<>();
        ArrayList<ExpenditureCompact> etcList = new ArrayList<>();

        int etcCnt = 0;

        for (int i = 0; i < monthlyList.size(); i++) {
            MonthlyDiary monthlyDiary = monthlyList.get(i);

            if (i < 3) {
                switch (i) {
                    case 0:
                        tvFirstCat.setText(monthlyDiary.getName());
                        tvFirstCnt.setText(monthlyDiary.getCount() + "번");
                        firstList.addAll(monthlyDiary.getExpenditureList());
                        break;
                    case 1:
                        tvSecondCat.setText(monthlyDiary.getName());
                        tvSecondCnt.setText(monthlyDiary.getCount() + "번");
                        secondList.addAll(monthlyDiary.getExpenditureList());
                        break;
                    case 2:
                        tvThirdCat.setText(monthlyDiary.getName());
                        tvThirdCnt.setText(monthlyDiary.getCount() + "번");
                        thirdList.addAll(monthlyDiary.getExpenditureList());
                        break;
                }
            } else {
                etcCnt += monthlyDiary.getCount();
                etcList.addAll(monthlyDiary.getExpenditureList());
            }
        }

        tvEtcCnt.setText(etcCnt + "번");

        if (firstList.size() > 0) {
            btnFirstCat.setVisibility(View.VISIBLE);

            MonthlyDiaryAdapter monthlyDiaryAdapter = new MonthlyDiaryAdapter(firstList);
            rvFirstCat.setAdapter(monthlyDiaryAdapter);
            rvFirstCat.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnFirstCat.setVisibility(View.GONE);
            setGoneRecycler(false, rvFirstCat, ivFirstMore, ivFirstHold, tvFirstMore);
        }

        if (secondList.size() > 0) {
            btnSecondCat.setVisibility(View.VISIBLE);

            MonthlyDiaryAdapter monthlyDiaryAdapter = new MonthlyDiaryAdapter(secondList);
            rvSecondCat.setAdapter(monthlyDiaryAdapter);
            rvSecondCat.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnSecondCat.setVisibility(View.GONE);
            setGoneRecycler(false, rvSecondCat, ivSecondMore, ivSecondHold, tvSecondMore);
        }

        if (thirdList.size() > 0) {
            btnThirdCat.setVisibility(View.VISIBLE);

            MonthlyDiaryAdapter monthlyDiaryAdapter = new MonthlyDiaryAdapter(thirdList);
            rvThirdCat.setAdapter(monthlyDiaryAdapter);
            rvThirdCat.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnThirdCat.setVisibility(View.GONE);
            setGoneRecycler(false, rvThirdCat, ivThirdMore, ivThirdHold, tvThirdMore);
        }

        if (etcList.size() > 0) {
            btnEtcCat.setVisibility(View.VISIBLE);

            MonthlyDiaryAdapter monthlyDiaryAdapter = new MonthlyDiaryAdapter(etcList);
            rvEtcCat.setAdapter(monthlyDiaryAdapter);
            rvEtcCat.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnEtcCat.setVisibility(View.GONE);
            setGoneRecycler(false, rvEtcCat, ivEtcMore, ivEtcHold, tvEtcMore);
        }
    }

    private void setGoneRecycler(boolean visible, RecyclerView rvCat, ImageView ivMore, ImageView ivHold, TextView tvMore) {
        if (visible) {
            rvCat.setVisibility(View.VISIBLE);
            ivHold.setVisibility(View.VISIBLE);
            ivMore.setVisibility(View.GONE);
            tvMore.setText("접기");
        } else {
            rvCat.setVisibility(View.GONE);
            ivHold.setVisibility(View.GONE);
            ivMore.setVisibility(View.VISIBLE);
            tvMore.setText("더보기");
        }
    }

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
