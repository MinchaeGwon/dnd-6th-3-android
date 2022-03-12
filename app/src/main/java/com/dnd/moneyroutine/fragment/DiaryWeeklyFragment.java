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
import com.dnd.moneyroutine.adapter.EmotionAdapter;
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
import java.util.HashMap;

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
    private RecyclerView rvEmotion;

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
        rvEmotion = v.findViewById(R.id.rv_week_emotion);

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

    // 주별 다이어리 가져오기 : 날짜, 감정
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

    // 일별 다이어리 가져오기 : 감정별로 소비 내역 가져옴
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

                        if (dailyList.size() > 0) {
                            EmotionAdapter emotionAdapter = new EmotionAdapter(dailyList);
                            rvEmotion.setAdapter(emotionAdapter);
                            rvEmotion.setLayoutManager(new LinearLayoutManager(getContext()));
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
}
