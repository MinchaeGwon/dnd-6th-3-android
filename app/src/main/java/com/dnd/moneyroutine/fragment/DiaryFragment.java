package com.dnd.moneyroutine.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.MainActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.enums.FooterEnum;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragment extends Fragment {
    private final static String TAG = "DiaryFragment";

    private ConstraintLayout clDiary;

    private Window window;
    private List<Fragment> fragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        clDiary = view.findViewById(R.id.cl_diary);

        tabInit(view);
    }

    // tab view 초기화 메소드
    private void tabInit(View v) {
        fragmentList = new ArrayList<>();
        fragmentList.add(null);
        fragmentList.add(null);

        TabLayout tlDiary = v.findViewById(R.id.tl_diary);

        // 초기 fragment setting
        tlDiary.selectTab(tlDiary.getTabAt(0));
        setFragment(0);

        tlDiary.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        if (position == 0) {
            window.setStatusBarColor(Color.parseColor("#F8F9FA"));
            clDiary.setBackgroundColor(Color.parseColor("#F8F9FA"));
        } else {
            window.setStatusBarColor(ContextCompat.getColor(getContext(), android.R.color.white));
            clDiary.setBackgroundColor(Color.WHITE);
        }

        if (fragmentList.get(position) != null) {
            getChildFragmentManager().beginTransaction().show(fragmentList.get(position)).commit();
            return;
        }

        fragmentList.set(position, getFragment(position));
        getChildFragmentManager().beginTransaction().add(R.id.fl_diary, fragmentList.get(position)).commit();
    }

    private Fragment getFragment(int position) {
        switch (position) {
            case 0:
                return new DiaryWeeklyFragment();
            case 1:
                return new DiaryMonthlyFragment();
        }
        return null;
    }
}
