package com.dnd.moneyroutine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ExpenditureFragment extends Fragment {
    private final static String TAG = "ExpenditureFragment";

    private TabLayout tlExpenditure;
    private List<Fragment> fragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        tabInit(view);
    }

    private void initView(View v){
        tlExpenditure = v.findViewById(R.id.tl_expenditure);

    }

    private void tabInit(View v){

        fragmentList=new ArrayList<>();
        fragmentList.add(null);
        fragmentList.add(null);

        tlExpenditure.selectTab(tlExpenditure.getTabAt(0));
        setFragment(0);

        tlExpenditure.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void setFragment(int position){
        if (fragmentList.get(position) != null) {
            getChildFragmentManager().beginTransaction().show(fragmentList.get(position)).commit();
            return;
        }

        fragmentList.set(position, getFragment(position));
        getChildFragmentManager().beginTransaction().add(R.id.fl_expenditure, fragmentList.get(position)).commit();
    }

    private Fragment getFragment(int position){
        switch (position) {
            case 0:
                return new ExpenditureWeeklyFragment();
            case 1:
                return new ExpenditureMonthlyFragment();
        }
        return null;
    }
}
