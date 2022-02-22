package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.AddExpenseActivity;
import com.dnd.moneyroutine.OnboardingCategoryActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.ExpenseCategoryAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.GoalCategory;
import com.dnd.moneyroutine.dto.GoalInfo;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 이번 달 fragment
public class MainCurrentMonthFragment extends Fragment {

    private final static String TAG = "CurrentMonthFragment";

    private ConstraintLayout clEmpty;
    private ConstraintLayout clGoalBudget;
    private ConstraintLayout clGoalCat;

    private TextView tvEmptyYearMonth;
    private TextView tvEmptyDate;
    private Button btnAddGoal;

    private ConstraintLayout btnAddExpense;
    private TextView tvYearMonth;
    private TextView tvDate;
    private TextView tvRemain;
    private TextView tvTotalBudget;

    private TextView tvTotalExpense;
    private RecyclerView rvCategory;

    private boolean isGoalExist;
    private String token;
    private LocalDate today;

    public MainCurrentMonthFragment() {}

    public MainCurrentMonthFragment(boolean isGoalExist) {
        this.isGoalExist = isGoalExist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_current_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initField();
        checkGoal();
    }

    private void initView(View v) {
        clEmpty = v.findViewById(R.id.cl_empty_goal);
        clGoalBudget = v.findViewById(R.id.cl_current_goal_budget);
        clGoalCat = v.findViewById(R.id.cl_current_goal_cat);

        tvEmptyYearMonth = v.findViewById(R.id.tv_empty_year_month);
        tvEmptyDate = v.findViewById(R.id.tv_empty_date);

        tvYearMonth = v.findViewById(R.id.tv_current_year_month);
        tvDate = v.findViewById(R.id.tv_current_date);

        tvRemain = v.findViewById(R.id.tv_current_remain);
        tvTotalBudget = v.findViewById(R.id.tv_current_total_budget);

        tvTotalExpense = v.findViewById(R.id.tv_current_total_expense);
        rvCategory = v.findViewById(R.id.rv_current_category);

        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddGoal = v.findViewById(R.id.btn_add_goal);
        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OnboardingCategoryActivity.class);
                startActivity(intent);
            }
        });

        btnAddExpense = v.findViewById(R.id.cl_main_add_expenditure);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddExpenseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initField() {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);

        today = LocalDate.now();

        tvEmptyYearMonth.setText(today.getYear() + "년 " + today.getMonthValue() + "월");
        tvEmptyDate.setText(today.getMonthValue() + "월 1일 ~ " + today.getMonthValue() + "월 " + today.lengthOfMonth() + "일");

        tvYearMonth.setText(today.getYear() + "년 " + today.getMonthValue() + "월");
        tvDate.setText(today.getMonthValue() + "월 1일 ~ " + today.getMonthValue() + "월 " + today.lengthOfMonth() + "일");
    }

    // 사용자가 목표 설정한 적이 있는지 확인
    private void checkGoal() {
        if (isGoalExist) {
            clEmpty.setVisibility(View.GONE);
            clGoalBudget.setVisibility(View.VISIBLE);
            clGoalCat.setVisibility(View.VISIBLE);

            getCurrentGoalInfo();
        } else {
            clEmpty.setVisibility(View.VISIBLE);
            clGoalBudget.setVisibility(View.GONE);
            clGoalCat.setVisibility(View.GONE);
        }
    }

    // 이번 달 목표 정보 가져오기
    private void getCurrentGoalInfo() {
        // 헤더에 토큰 추가하는 코드
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMainGoalList(today);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (responseJson.get("data") != null) {
                            Gson gson = new Gson();
                            GoalInfo responseGoal = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<GoalInfo>() {}.getType());

                            if (responseGoal != null) {
                                setGoalInfo(responseGoal);
                            }
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

    private void setGoalInfo(GoalInfo goalInfo) {
        DecimalFormat myFormatter = new DecimalFormat("###,###");

        String remain = myFormatter.format(goalInfo.getRemainder());
        tvRemain.setText(remain + "원");

        String totalBudget = myFormatter.format(goalInfo.getTotalBudget());
        tvTotalBudget.setText(totalBudget + "원 중");

        int totalExpense = 0;
        for (GoalCategory goalCategoryDetail : goalInfo.getGoalCategoryList()) {
            totalExpense += goalCategoryDetail.getTotalExpense();
        }

        String strTotalExpense = myFormatter.format(totalExpense);
        tvTotalBudget.setText("전체 " + strTotalExpense + "원 지출");

        ExpenseCategoryAdapter expenseCategoryAdapter = new ExpenseCategoryAdapter(goalInfo.getGoalCategoryList(), true);
        rvCategory.setAdapter(expenseCategoryAdapter);
    }
}
