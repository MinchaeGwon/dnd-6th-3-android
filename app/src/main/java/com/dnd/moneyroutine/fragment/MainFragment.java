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
import com.dnd.moneyroutine.SettingActivity;
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
                return new MainCurrentMonthFragment();
            case 1:
                return new MainPastRecordFragment();
        }
        return null;
    }
}
