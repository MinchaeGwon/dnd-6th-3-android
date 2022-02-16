package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.BudgetUpdateActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.SocialLoginActivity;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

// 메인 fragment
public class MainFragment extends Fragment {

    private final static String TAG = "MainFragment";

    private List<Fragment> fragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        tabInit(view);
    }

    private void initView(View v) {
        Button btnLogout = v.findViewById(R.id.btn_main_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutToServer();
            }
        });

        ImageButton ibUpdateBudget = v.findViewById(R.id.ib_main_update_budget);
        ibUpdateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BudgetUpdateActivity.class);
                startActivity(intent);
            }
        });

        ImageButton ibSetting = v.findViewById(R.id.ib_main_setting);
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                return new MainCurrentMonthFragment();
            case 1:
                return new MainPastRecordFragment();
        }
        return null;
    }

    // 임시 로그아웃 버튼 설정
    private void logoutToServer() {
        // 서버 구현 전이기 때문에 SharedPreferences에서만 토큰을 삭제
        removeSharedPreferences();
        afterLogoutMoveActivity();

//        String token = PreferenceManager.getToken(getContext(), Constants.tokenKey); // 토큰 가져오기
//
//        // 헤더에 토큰 추가하는 코드
//        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
//        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
//        RetrofitService retroService = retrofit.create(RetrofitService.class);
//
//        Call<JsonObject> call = retroService.signout();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if(response.isSuccessful()) {
//                    // sharedPreferences가 보관하고 있던 토큰을 삭제
//                    removeSharedPreferences();
//                    afterLogoutMoveActivity();
//                    Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // SharedPreferences에서 토큰 제거
    private void removeSharedPreferences() {
        PreferenceManager.removeKey(getContext(), Constants.tokenKey);
    }

    // 로그아웃 후에 어디로 이동할지 결정하는 메소드 -> SocialLoginActivity로 이동
    private void afterLogoutMoveActivity() {
        Intent intent = new Intent(getContext(), SocialLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
