package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.BudgetUpdateActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.SettingActivity;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 메인 fragment
public class MainFragment extends Fragment {

    private final static String TAG = "MainFragment";

    private AlertDialog goalDialog;
    private List<Fragment> fragmentList;
    private boolean isGoalExist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        checkGoal(view, false);
    }

    private void initView(View v) {
        ImageButton ibUpdateBudget = v.findViewById(R.id.ib_main_update_budget);
        ibUpdateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGoal(v, true);
            }
        });

        ImageButton ibSetting = v.findViewById(R.id.ib_main_setting);
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    // tab view 초기화 메소드
    private void tabInit(View v) {
        fragmentList = new ArrayList<>();
        fragmentList.add(null);
        fragmentList.add(null);

        TabLayout tlGoal = v.findViewById(R.id.tl_main_goal);

        // 초기 fragment setting
        tlGoal.selectTab(tlGoal.getTabAt(0));
        setFragment(0);

        tlGoal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                setFragment(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (fragmentList.get(position) != null) {
                    getChildFragmentManager().beginTransaction().hide(fragmentList.get(position)).commit();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setFragment(int position) {
        if (fragmentList.get(position) != null) {
            getChildFragmentManager().beginTransaction().show(fragmentList.get(position)).commit();
            return;
        }

        fragmentList.set(position, getFragment(position));
        getChildFragmentManager().beginTransaction().add(R.id.fl_main_record, fragmentList.get(position)).commit();
    }

    private Fragment getFragment(int position) {
        switch (position) {
            case 0:
                return new MainCurrentMonthFragment(isGoalExist);
            case 1:
                return new MainPastRecordFragment();
        }
        return null;
    }

    private void setGoalDialog() {
        if (goalDialog != null) return;
        makeGoalDialog();
    }

    // 기록 종료 확인 다이얼로그 만들기
    private void makeGoalDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setView(view);
        goalDialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);

        tvTitle.setText("예산 정보가 없습니다");
        tvContent.setText("새로운 예산 계획을 세워주세요!");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goalDialog.dismiss();
            }
        });
    }

    // 사용자가 목표 설정한 적이 있는지 확인
    private void checkGoal(View v, boolean update) {
        String token = PreferenceManager.getToken(getContext(), Constants.tokenKey);
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();

        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.checkGoal();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, "11111" + responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        isGoalExist = responseJson.get("data").getAsBoolean();

                        if (update) {
                            // 목표 설정하지 않았다면 온보딩 화면으로 이동
                            if (isGoalExist) {
                                Intent intent = new Intent(getContext(), BudgetUpdateActivity.class);
                                startActivity(intent);
                            } else {
                                setGoalDialog();
                                goalDialog.show();
                            }
                        } else {
                            tabInit(v);
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
}
