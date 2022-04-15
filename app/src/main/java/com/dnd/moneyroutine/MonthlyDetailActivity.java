package com.dnd.moneyroutine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.adapter.MonthlyDetailAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.CategoryType;
import com.dnd.moneyroutine.dto.MonthlyDetail;
import com.dnd.moneyroutine.dto.MonthlyExpenditure;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.LocalDateSerializer;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MonthlyDetailActivity extends AppCompatActivity {

    private static final String TAG = "MonthlyDetailActivity";

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvCategoryName;
    private TextView tvPercent;
    private TextView tvTotal;
    private RecyclerView rcContent;

    private String token;

    private LocalDate startDate;
    private LocalDate endDate;

    private String color;
    private CategoryType type;
    private String categoryName;
    private int percent;
    private int totalExpense;

    private boolean etc;
    private ArrayList<CategoryType> etcCategoryTypes;
    private ArrayList<String> etcCategoryNames;

    private HashMap<LocalDate, ArrayList<MonthlyDetail>> detailMap;
    private MonthlyDetailAdapter adapter;

    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_detail);

        token = PreferenceManager.getToken(this, Constants.tokenKey);

        initView();
        initField();
        setCategoryInfo();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back_monthly_detail);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvTitle = findViewById(R.id.tv_monthly_detail_title);
        tvCategoryName = findViewById(R.id.tv_month_detail_category);
        tvPercent = findViewById(R.id.tv_monthly_detail_percent);
        tvTotal = findViewById(R.id.tv_monthly_detail_total);
        rcContent = findViewById(R.id.rc_monthly_detail);
    }

    private void initField() {
        detailMap = new HashMap<>();

        Intent intent = getIntent();

        startDate = (LocalDate) intent.getSerializableExtra("startDate");
        endDate = (LocalDate) intent.getSerializableExtra("endDate");

        type = (CategoryType) intent.getSerializableExtra("categoryType");

        color = intent.getStringExtra("categoryColor");
        categoryName = intent.getStringExtra("categoryName");
        percent = intent.getIntExtra("percentage",0);
        totalExpense = intent.getIntExtra("totalExpense",0);

        etc = intent.getBooleanExtra("etc", false);

        if (etc) {
            etcCategoryTypes = (ArrayList<CategoryType>) intent.getSerializableExtra("etcCategoryType");
            etcCategoryNames = (ArrayList<String>) intent.getSerializableExtra("etcCategoryName");
        }
    }

    private void setCategoryInfo() {
        Log.d(TAG,  "start : " + startDate.toString() + ", end : " + endDate.toString() +  ", categoryId : " + type.getCategoryId() + ", custom : " + type.isCustom());

        LocalDate today = LocalDate.now();
        if (today.getYear() != startDate.getYear()) {
            tvCategoryName.setText(startDate.getYear() + "년 " + startDate.getMonthValue() + "월 " + categoryName + " 지출액");
        } else {
            tvCategoryName.setText(startDate.getMonthValue() + "월 " + categoryName + " 지출액");
        }

        tvPercent.setText(percent + "%");
        tvPercent.setTextColor(Color.parseColor(color));

        tvTitle.setText(categoryName);
        tvTotal.setText("총 " + decimalFormat.format(totalExpense) + "원");

        if (etc) {
            for (int i = 0; i < etcCategoryTypes.size(); i++) {
                CategoryType etcType = etcCategoryTypes.get(i);
                getDetailServer(startDate, endDate, etcType.getCategoryId(), etcType.isCustom(), i == etcCategoryTypes.size() - 1);
            }
        } else {
            getDetailServer(startDate, endDate, type.getCategoryId(), type.isCustom(), true);
        }
    }

    // 월별 카테고리 소비 상세 내역 가져오기
    private void getDetailServer(LocalDate startDate, LocalDate endDate, int categoryId, boolean custom, boolean last) {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMonthlyDetail(startDate, endDate, categoryId, custom);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
                            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();

                            HashMap<LocalDate, ArrayList<MonthlyDetail>> responseDetail = gson.fromJson(responseJson.getAsJsonObject("data"),
                                    new TypeToken<HashMap<LocalDate, ArrayList<MonthlyDetail>>>() {}.getType());

                            if (etc) {
                                responseDetail.forEach((date, detail) -> {
                                    // 해당 날짜가 이미 있는 경우 리스트 추가만 함
                                    if (detailMap.containsKey(date)) {
                                        detailMap.get(date).addAll(detail);
                                    } else {
                                        detailMap.put(date, detail);
                                    }
                                });
                            } else {
                                detailMap.putAll(responseDetail);
                            }

                            if (last) {
                                setExpenditureInfo();
                            }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MonthlyDetailActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 일별 소비내역 정보 바인딩
    private void setExpenditureInfo() {
        ArrayList<MonthlyExpenditure> monthlyList = new ArrayList<>();

        detailMap.forEach((date, detail) -> monthlyList.add(new MonthlyExpenditure(date, detail)));
        Collections.sort(monthlyList);

        MonthlyDetailAdapter monthlyDetailAdapter = new MonthlyDetailAdapter(monthlyList, etc);
        rcContent.setLayoutManager(new LinearLayoutManager(MonthlyDetailActivity.this));
        rcContent.setAdapter(monthlyDetailAdapter);
    }
}