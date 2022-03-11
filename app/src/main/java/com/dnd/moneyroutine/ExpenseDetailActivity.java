package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.adapter.GoalCategoryGridAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.ExpenditureDetail;
import com.dnd.moneyroutine.dto.CategoryCompact;
import com.dnd.moneyroutine.enums.EmotionEnum;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ExpenseDetailActivity extends AppCompatActivity {

    private TextView tvCategory;
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvExpense;
    private RecyclerView rvCategory;
    private RadioGroup rgEmotion;
    private TextView tvEmotionDetail;

    private String token;
    private EmotionEnum emotion;
    private ExpenditureDetail expenditure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        token = PreferenceManager.getToken(this, Constants.tokenKey);

        Intent intent = getIntent();
        emotion = (EmotionEnum) intent.getSerializableExtra("emotion");
        expenditure = (ExpenditureDetail) intent.getSerializableExtra("expenditureDetail");

        initView();
        setDetailInfo();
        getGoalCategory();
    }

    private void initView() {
        ImageButton ibBack = findViewById(R.id.ib_dtl_expense_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvCategory = findViewById(R.id.tv_dtl_expense_cat);
        tvDate = findViewById(R.id.tv_dtl_expense_date);
        tvContent = findViewById(R.id.tv_dtl_expense_content);
        tvExpense = findViewById(R.id.tv_dtl_expense_money);
        rvCategory = findViewById(R.id.rv_dtl_expense_category);
        rgEmotion = findViewById(R.id.rg_dtl_expense_emotion);
        tvEmotionDetail = findViewById(R.id.tv_dtl_expense_emotion_content);
    }

    private void setDetailInfo() {
        tvCategory.setText(expenditure.getName());

        LocalDate date = expenditure.getDate();

        if (date.getYear() == LocalDate.now().getYear()) {
            tvDate.setText(date.getMonthValue() + "월 " + date.getDayOfMonth() + "일");
        } else {
            tvDate.setText(date.getYear() + "년 " + date.getMonthValue() + "월 " + date.getDayOfMonth() + "일");
        }

        tvContent.setText(expenditure.getExpenseDetail());

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String expense = myFormatter.format(expenditure.getExpense());
        tvExpense.setText(expense + "원");

        rgEmotion.check(mappingEmotionId());
        tvEmotionDetail.setText(expenditure.getEmotionDetail());
    }

    // 사용자가 선택한 카테고리 가져오기
    private void getGoalCategory() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getGoalCategory();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                        Gson gson = new Gson();
                        ArrayList<CategoryCompact> responseCategory = gson.fromJson(jsonArray, new TypeToken<ArrayList<CategoryCompact>>() {}.getType());

                        if (responseCategory != null) {
                            setGoalCategory(responseCategory);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ExpenseDetailActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGoalCategory(ArrayList<CategoryCompact> categoryList) {
        GoalCategoryGridAdapter goalCategoryGridAdapter = new GoalCategoryGridAdapter(categoryList, expenditure.getCategoryId(), expenditure.isCustom(), true);

        rvCategory.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategory.setAdapter(goalCategoryGridAdapter);
    }

    private int mappingEmotionId() {
        switch (emotion) {
            case GOOD:
                return R.id.rb_dtl_emotion_good;
            case SOSO:
                return R.id.rb_dtl_emotion_soso;
            case BAD:
                return R.id.rb_dtl_emotion_bad;
        }
        return -1;
    }
}