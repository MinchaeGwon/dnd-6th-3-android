package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dnd.moneyroutine.adapter.GoalCategoryGridAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.GoalCategoryCompact;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 지출 분야 추가 activity
public class AddExpenseCatActivity extends AppCompatActivity {

    private static final String TAG = "AddCategoryActivity";

    private ImageButton ibBack;
    private ImageButton ibCancel;

    private RecyclerView rvCategory;
    private LinearLayout btnAddCustomCat;

    private String token;
    private int goalId;
    private GoalCategoryCompact selectCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_cat);

        token = PreferenceManager.getToken(this, Constants.tokenKey);
        goalId = getIntent().getIntExtra("goalId", -1);

        initView();
        setListener();
        getExceptCategory();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_add_cat_back);
        ibCancel = findViewById(R.id.ib_add_cat_cancel);

        rvCategory = findViewById(R.id.rv_except_category);

        btnAddCustomCat = findViewById(R.id.btn_add_expense_cat);
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddCustomCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DirectCustomCategoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("goalId", goalId);

                startActivity(intent);
                finish();
            }
        });
    }

    // 사용자가 선택하지 않은 나머지 분야 가져오기
    private void getExceptCategory() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getExceptCategory(goalId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
                            JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                            Gson gson = new Gson();
                            ArrayList<GoalCategoryCompact> responseCategory = gson.fromJson(jsonArray, new TypeToken<ArrayList<GoalCategoryCompact>>() {}.getType());

                            if (responseCategory != null) {
                                setExceptCategory(responseCategory);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AddExpenseCatActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setExceptCategory(ArrayList<GoalCategoryCompact> categoryList) {
        GoalCategoryGridAdapter goalCategoryGridAdapter = new GoalCategoryGridAdapter(categoryList);
        goalCategoryGridAdapter.setOnItemClickListener(new GoalCategoryGridAdapter.OnItemClickListener() {
            @Override
            public void onClick(GoalCategoryCompact category) {
                selectCategory = category;
                addBasicCategory();
            }
        });

        rvCategory.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategory.setAdapter(goalCategoryGridAdapter);
    }

    // 기본 카테고리 지출 분야로 추가
    private void addBasicCategory() {

    }
}