package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.OnboardingCategoryActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.ExpenseCategoryAdapter;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.YearMonthPickerDialog;
import com.dnd.moneyroutine.dto.GoalCategoryDetail;
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

// 지난 기록 fragment
public class MainPastRecordFragment extends Fragment  {

    private final static String TAG = "PastRecordFragment";

    private ConstraintLayout clNewMonthStart;
    private TextView tvStartMonth;
    private Button btnNewBudget;
    private Button btnGoalContinue;

    private ConstraintLayout clPastEmpty;
    private TextView tvEmptyDate;

    private ConstraintLayout clPastInfo;

    private LinearLayout btnSelectMonth;
    private TextView tvSelectMonth;
    private TextView tvPastYearMonth;
    private TextView tvPastDate;
    private TextView tvGoalSuccess;
    private TextView tvPastRemain;
    private TextView tvPastBudget;
    private TextView tvPastExpense;
    private ProgressBar pbPastExpense;

    private RecyclerView rvCategory;

    private MainFragment parentFragment;
    private String token;

    private boolean newMonth;
    private LocalDate selectDate;

    public MainPastRecordFragment() {}

    public MainPastRecordFragment(boolean newMonth, MainFragment parentFragment) {
        this.newMonth = newMonth;
        this.parentFragment = parentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_past_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initField();
        setListener();

        getPastGoalInfo(selectDate);
    }

    private void initView(View v) {
        clNewMonthStart = v.findViewById(R.id.cl_new_month_start);
        tvStartMonth = v.findViewById(R.id.tv_start_month);
        btnNewBudget = v.findViewById(R.id.btn_new_budget);
        btnGoalContinue = v.findViewById(R.id.btn_goal_continue);

        clPastEmpty = v.findViewById(R.id.cl_past_empty);
        tvEmptyDate = v.findViewById(R.id.tv_past_empty_year_month);

        clPastInfo = v.findViewById(R.id.cl_past_month_record);

        tvSelectMonth = v.findViewById(R.id.tv_select_month);
        tvPastYearMonth = v.findViewById(R.id.tv_past_year_month);
        tvPastDate = v.findViewById(R.id.tv_past_date);
        tvGoalSuccess = v.findViewById(R.id.tv_past_goal_success);
        tvPastRemain = v.findViewById(R.id.tv_past_remain);
        tvPastBudget = v.findViewById(R.id.tv_past_total_budget);
        tvPastExpense = v.findViewById(R.id.tv_past_total_expense);
        pbPastExpense = v.findViewById(R.id.pb_past_remain_budget);

        rvCategory = v.findViewById(R.id.rv_past_category);
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSelectMonth = v.findViewById(R.id.ll_select_month);
    }

    private void initField() {
        token = PreferenceManager.getToken(getContext(), Constants.tokenKey);

        selectDate = LocalDate.now().minusMonths(1); // 이전 달부터 보여주기 때문에 -1 해줌

        tvEmptyDate.setText(selectDate.getYear() + "년 " + selectDate.getMonthValue() + "월");

        tvSelectMonth.setText(Common.getMainLocalDateToString(selectDate));
        tvPastYearMonth.setText(selectDate.getYear() + "년 " + selectDate.getMonthValue() + "월");
        tvPastDate.setText(selectDate.getMonthValue() + "/1 ~ " + selectDate.getMonthValue() + "/" + selectDate.lengthOfMonth());

        if (newMonth) {
            clNewMonthStart.setVisibility(View.VISIBLE);
            btnSelectMonth.setVisibility(View.GONE);

            tvStartMonth.setText((selectDate.getMonthValue() + 1) + "월이 시작됐어요");
        }
    }

    private void setListener() {
        btnNewBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OnboardingCategoryActivity.class));
            }
        });

        btnGoalContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeContinueGoal();
            }
        });

        btnSelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 연도, 월 선택 다이얼로그 띄우기
                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(selectDate, true);
                yearMonthPickerDialog.show(getActivity().getSupportFragmentManager(), "YearMonthPickerDialog");

                yearMonthPickerDialog.setOnSelectListener(new YearMonthPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(LocalDate date) {
                        selectDate = date;

                        tvEmptyDate.setText(date.getYear() + "년 " + date.getMonthValue() + "월");

                        tvSelectMonth.setText(Common.getMainLocalDateToString(date));
                        tvPastYearMonth.setText(date.getYear() + "년 " + date.getMonthValue() + "월");
                        tvPastDate.setText(date.getMonthValue() + "/1 ~ " + date.getMonthValue() + "/" + date.lengthOfMonth());

                        getPastGoalInfo(date);
                    }
                });
            }
        });
    }

    // 월별 목표 기록 가져오기
    private void getPastGoalInfo(LocalDate date) {
        // 헤더에 토큰 추가
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMainGoalList(date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
                            Gson gson = new Gson();
                            GoalInfo responseGoal = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<GoalInfo>() {}.getType());

                            if (responseGoal != null) {
                                setGoalInfo(responseGoal);

                                clPastInfo.setVisibility(View.VISIBLE);
                                clPastEmpty.setVisibility(View.GONE);
                            }
                        } else {
                            clPastInfo.setVisibility(View.GONE);
                            clPastEmpty.setVisibility(View.VISIBLE);
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

    // 이전 목표를 이어갈 경우
    private void executeContinueGoal() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.continueGoal();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        // tab 새로고침
                        parentFragment.getCurrentGoalInfo();
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
        pbPastExpense.setMax(goalInfo.getTotalBudget());
        pbPastExpense.setProgress(goalInfo.getTotalBudget() - goalInfo.getRemainder());

        tvGoalSuccess.setText(goalInfo.getGoalState().getState());

        DecimalFormat myFormatter = new DecimalFormat("###,###");

        String remain = myFormatter.format(goalInfo.getRemainder());
        tvPastRemain.setText(remain + "원 아낌");

        String totalBudget = myFormatter.format(goalInfo.getTotalBudget());
        tvPastBudget.setText(totalBudget + "원 중");

        int totalExpense = goalInfo.getTotalBudget() - goalInfo.getRemainder();
        String strTotalExpense = myFormatter.format(totalExpense);
        tvPastExpense.setText("전체 " + strTotalExpense + "원 지출");

        ExpenseCategoryAdapter expenseCategoryAdapter = new ExpenseCategoryAdapter(goalInfo.getGoalCategoryList(), false);
        rvCategory.setAdapter(expenseCategoryAdapter);
    }
}
