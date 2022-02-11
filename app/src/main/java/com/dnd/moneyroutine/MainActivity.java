package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dnd.moneyroutine.fragment.ChallengeFragment;
import com.dnd.moneyroutine.fragment.DiaryFragment;
import com.dnd.moneyroutine.fragment.ExpenditureFragment;
import com.dnd.moneyroutine.fragment.MainFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TabLayout tlMain;

    private List<Fragment> fragmentList;
    private List<ImageView> bottomIconList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        Configuration configuration = new Configuration(newBase.getResources().getConfiguration());
        configuration.fontScale = 1;
        applyOverrideConfiguration(configuration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initField();
        initTabView();
        initFragment();
        setTab();
    }

    private void initField() {
        tlMain = findViewById(R.id.tl_main_tab);
        fragmentList = new ArrayList<>();
        bottomIconList = new ArrayList<>();
    }

    // custom tab view 만들기
    private void initTabView() {
        for (int i = 0; i < 4; i++) {
            View view = getLayoutInflater().inflate(R.layout.view_bottom_tab, null);
            view.setOnClickListener(getViewOnClick(i));

            ImageButton imgIcon = view.findViewById(R.id.btn_tab_image);
            imgIcon.setOnClickListener(getViewOnClick(i));
            imgIcon.setImageResource(mappingUnpressedIcon(i));

            tlMain.addTab(tlMain.newTab().setCustomView(view));
            bottomIconList.add(i, imgIcon);
        }
    }

    // 탭 클릭 리스너 : 탭 선택이 제대로 되지 않을 경우를 위해 해주는 것
    private View.OnClickListener getViewOnClick(int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlMain.getTabAt(position).select();
            }
        };
    }

    // fragment 초기화
    private void initFragment() {
        fragmentList.add(new MainFragment());
        fragmentList.add(null);
        fragmentList.add(null);
        fragmentList.add(null);

        getSupportFragmentManager().beginTransaction().add(R.id.fl_main_select, fragmentList.get(0)).commit();
        setBottomIcon(0, true);
    }

    // 탭 설정
    private void setTab() {
        tlMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                setFragment(position);
                setBottomIcon(position, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                setBottomIcon(position, false);
                hidePreviousFragment(position);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // fragment setting
    private void setFragment(int position) {
        if (fragmentList.get(position) != null) {
            getSupportFragmentManager().beginTransaction().show(fragmentList.get(position)).commit();
            return;
        }

        fragmentList.set(position, mappingFragment(position));
        getSupportFragmentManager().beginTransaction().add(R.id.fl_main_select, fragmentList.get(position)).commit();
    }

    // 하단바 아이콘 정보 바인딩
    private void setBottomIcon(int position, boolean pressed) {
        bottomIconList.get(position).setImageResource(pressed ? mappingPressedIcon(position) : mappingUnpressedIcon(position));
    }

    // fragment 만들기
    private Fragment mappingFragment(int position) {
        switch (position) {
            case 0:
                return new MainFragment();
            case 1:
                return new ExpenditureFragment();
            case 2:
                return new DiaryFragment();
            case 3:
                return new ChallengeFragment();
        }
        return null;
    }

    // 이전 fragment 숨기기
    private void hidePreviousFragment(int position) {
        if (fragmentList.get(position) != null) {
            getSupportFragmentManager().beginTransaction().hide(fragmentList.get(position)).commit();
        }
    }

    // 선택된 탭 아이콘 변경
    private int mappingPressedIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.icon_fill_home;
            case 1:
                return R.drawable.icon_fill_expenditure;
            case 2:
                return R.drawable.icon_fill_diary;
            case 3:
                return R.drawable.icon_fill_challenge;
        }
        return 0;
    }

    // 이전에 선택했던 탭 아이콘 변경
    private int mappingUnpressedIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.icon_empty_home;
            case 1:
                return R.drawable.icon_empty_expenditure;
            case 2:
                return R.drawable.icon_empty_diary;
            case 3:
                return R.drawable.icon_empty_challenge;
        }
        return 0;
    }
}