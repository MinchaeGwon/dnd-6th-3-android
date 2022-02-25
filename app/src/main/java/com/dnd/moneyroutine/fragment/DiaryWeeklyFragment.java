package com.dnd.moneyroutine.fragment;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.WeeklyCalendarAdapter;
import com.dnd.moneyroutine.adapter.WeeklyDiaryAdapter;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.WeekPickerDialog;
import com.dnd.moneyroutine.dto.DailyDiary;
import com.dnd.moneyroutine.dto.ExpenditureDetail;
import com.dnd.moneyroutine.dto.WeeklyDiary;
import com.dnd.moneyroutine.enums.EmotionEnum;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.LocalDateSerializer;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DiaryWeeklyFragment extends Fragment {

    private static final String TAG = "DiaryWeekly";
    private static final int DAYS_COUNT = 7;

    private LinearLayout btnSelectWeek;
    private TextView tvSelectWeek;
    private RecyclerView rvCalendar;

    private TextView tvDate;

    private LinearLayout btnGood;
    private TextView tvGoodCnt;
    private TextView tvGoodMoney;
    private ImageView ivGoodMore;
    private ImageView ivGoodHold;
    private RecyclerView rvGood;

    private LinearLayout btnSoso;
    private TextView tvSosoCnt;
    private TextView tvSosoMoney;
    private ImageView ivSosoMore;
    private ImageView ivSosoHold;
    private RecyclerView rvSoso;

    private LinearLayout btnBad;
    private TextView tvBadCnt;
    private TextView tvBadMoney;
    private ImageView ivBadMore;
    private ImageView ivBadHold;
    private RecyclerView rvBad;

    private String token;
    private Calendar selectDate;
    private Calendar today;
    private ArrayList<WeeklyDiary> weeklyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_weekly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initField();
        setListener();

        getWeeklyDiary();
        getDailyDiary(selectDate);
    }

    private void initView(View v) {
        btnSelectWeek = v.findViewById(R.id.ll_select_week);
        tvSelectWeek = v.findViewById(R.id.tv_week_header);

        tvDate = v.findViewById(R.id.tv_week_date);

        btnGood = v.findViewById(R.id.ll_week_good);
        tvGoodCnt = v.findViewById(R.id.tv_week_good_cnt);
        tvGoodMoney = v.findViewById(R.id.tv_week_good_money);
        ivGoodMore = v.findViewById(R.id.iv_week_good_more);
        ivGoodHold = v.findViewById(R.id.iv_week_good_hold);
        rvGood = v.findViewById(R.id.rv_week_good_detail);

        btnSoso = v.findViewById(R.id.ll_week_soso);
        tvSosoCnt = v.findViewById(R.id.tv_week_soso_cnt);
        tvSosoMoney = v.findViewById(R.id.tv_week_soso_money);
        ivSosoMore = v.findViewById(R.id.iv_week_soso_more);
        ivSosoHold = v.findViewById(R.id.iv_week_soso_hold);
        rvSoso = v.findViewById(R.id.rv_week_soso_detail);

        btnBad = v.findViewById(R.id.ll_week_bad);
        tvBadCnt = v.findViewById(R.id.tv_week_bad_cnt);
        tvBadMoney = v.findViewById(R.id.tv_week_bad_money);
        ivBadMore = v.findViewById(R.id.iv_week_bad_more);
        ivBadHold = v.findViewById(R.id.iv_week_bad_hold);
        rvBad = v.findViewById(R.id.rv_week_bad_detail);

        rvCalendar = v.findViewById(R.id.rv_week_calendar);
    }

    private void initField() {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);

        today = Calendar.getInstance();
        selectDate = Calendar.getInstance();
        tvDate.setText((selectDate.get(Calendar.MONTH) + 1) + "월 " + selectDate.get(Calendar.DATE) + "일 소비 다이어리");
    }

    private void setListener() {
        btnSelectWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 연도, 월, 주 선택 다이얼로그 띄우기
                WeekPickerDialog weekPickerDialog = new WeekPickerDialog(selectDate);
                weekPickerDialog.show(getActivity().getSupportFragmentManager(), "YearMonthPickerDialog");

                weekPickerDialog.setOnSelectListener(new WeekPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(Calendar calendar) {
                        selectDate = calendar;
                        getWeeklyDiary();
                    }
                });
            }
        });

        btnGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvGood.getVisibility() == View.GONE) {
                    rvGood.setVisibility(View.VISIBLE);
                    ivGoodHold.setVisibility(View.VISIBLE);
                    ivGoodMore.setVisibility(View.GONE);
                } else {
                    rvGood.setVisibility(View.GONE);
                    ivGoodHold.setVisibility(View.GONE);
                    ivGoodMore.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvSoso.getVisibility() == View.GONE) {
                    rvSoso.setVisibility(View.VISIBLE);
                    ivSosoHold.setVisibility(View.VISIBLE);
                    ivSosoMore.setVisibility(View.GONE);
                } else {
                    rvSoso.setVisibility(View.GONE);
                    ivSosoHold.setVisibility(View.GONE);
                    ivSosoMore.setVisibility(View.VISIBLE);
                }
            }
        });

        btnBad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvBad.getVisibility() == View.GONE) {
                    rvBad.setVisibility(View.VISIBLE);
                    ivBadHold.setVisibility(View.VISIBLE);
                    ivBadMore.setVisibility(View.GONE);
                } else {
                    rvBad.setVisibility(View.GONE);
                    ivBadHold.setVisibility(View.GONE);
                    ivBadMore.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // 캘린더 설정
    public void setCalendar() {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) selectDate.clone();

        int year = selectDate.get(Calendar.YEAR);
        int month = selectDate.get(Calendar.MONTH) + 1;
        int week = selectDate.get(Calendar.WEEK_OF_MONTH);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작을 일요일로 설정
        int weekBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        calendar.add(Calendar.DAY_OF_MONTH, -weekBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK,1);
        }

        WeeklyCalendarAdapter weeklyCalendarAdapter = new WeeklyCalendarAdapter(cells, year, month, week, weeklyList);
        weeklyCalendarAdapter.setOnSelectListener(new WeeklyCalendarAdapter.OnSelectListener() {
            @Override
            public void onSelect(View v, Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                    tvDate.setText((calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.DATE) + "일 소비 다이어리");
                } else {
                    tvDate.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.DATE) + "일 소비 다이어리");
                }

                getDailyDiary(calendar);
            }
        });

        // update view
        rvCalendar.setAdapter(weeklyCalendarAdapter);
        rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));

        // update title
        tvSelectWeek.setText(Common.getWeeklyCalendarToString(selectDate));
    }

    private void getWeeklyDiary() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        int year = selectDate.get(Calendar.YEAR);
        int month = selectDate.get(Calendar.MONTH) + 1;
        int week = selectDate.get(Calendar.WEEK_OF_MONTH);

        Call<JsonObject> call = retroService.getWeeklyDiary(year, month, week);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
                        weeklyList = gson.fromJson(jsonArray, new TypeToken<ArrayList<WeeklyDiary>>() {}.getType());
                        setCalendar(); // 주별 감정 리스트를 가지고 캘린더 만듦
                   }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDailyDiary(Calendar calendar) {
        LocalDate date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getDailyDiary(date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
                        ArrayList<DailyDiary> dailyList = gson.fromJson(jsonArray, new TypeToken<ArrayList<DailyDiary>>() {}.getType());

                        setEmotionDiary(dailyList);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEmotionDiary(ArrayList<DailyDiary> dailyList) {
        ArrayList<ExpenditureDetail> goodList = new ArrayList<>();
        ArrayList<ExpenditureDetail> sosoList = new ArrayList<>();
        ArrayList<ExpenditureDetail> badList = new ArrayList<>();

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String expense;

        for (DailyDiary dailyDiary : dailyList) {
            switch (dailyDiary.getEmotion()) {
                case GOOD:
                    tvGoodCnt.setText(dailyDiary.getCount() + "개");

                    expense = myFormatter.format(dailyDiary.getAmount()) + "원";
                    tvGoodMoney.setText(expense);

                    goodList.addAll(dailyDiary.getExpenditureList());
                    break;
                case SOSO:
                    tvSosoCnt.setText(dailyDiary.getCount() + "개");

                    expense = myFormatter.format(dailyDiary.getAmount()) + "원";
                    tvSosoMoney.setText(expense);

                    sosoList.addAll(dailyDiary.getExpenditureList());
                    break;
                case BAD:
                    tvBadCnt.setText(dailyDiary.getCount() + "개");

                    expense = myFormatter.format(dailyDiary.getAmount()) + "원";
                    tvBadMoney.setText(expense);

                    badList.addAll(dailyDiary.getExpenditureList());
                    break;
            }
        }

        if (goodList.size() > 0) {
            btnGood.setVisibility(View.VISIBLE);

            WeeklyDiaryAdapter weeklyDiaryAdapter = new WeeklyDiaryAdapter(EmotionEnum.GOOD, goodList);
            rvGood.setAdapter(weeklyDiaryAdapter);
            rvGood.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnGood.setVisibility(View.GONE);

            rvGood.setVisibility(View.GONE);
            ivGoodMore.setVisibility(View.VISIBLE);
            ivGoodHold.setVisibility(View.GONE);
        }

        if (sosoList.size() > 0) {
            btnSoso.setVisibility(View.VISIBLE);

            WeeklyDiaryAdapter weeklyDiaryAdapter = new WeeklyDiaryAdapter(EmotionEnum.SOSO, sosoList);
            rvSoso.setAdapter(weeklyDiaryAdapter);
            rvSoso.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnSoso.setVisibility(View.GONE);

            rvSoso.setVisibility(View.GONE);
            ivSosoMore.setVisibility(View.VISIBLE);
            ivSosoHold.setVisibility(View.GONE);
        }

        if (badList.size() > 0) {
            btnBad.setVisibility(View.VISIBLE);

            WeeklyDiaryAdapter weeklyDiaryAdapter = new WeeklyDiaryAdapter(EmotionEnum.BAD, badList);
            rvBad.setAdapter(weeklyDiaryAdapter);
            rvBad.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            btnBad.setVisibility(View.GONE);

            rvBad.setVisibility(View.GONE);
            ivBadMore.setVisibility(View.VISIBLE);
            ivBadHold.setVisibility(View.GONE);
        }
    }
}
