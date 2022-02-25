package com.dnd.moneyroutine.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.YearMonthPickerDialog;

import java.time.LocalDate;

// 목표 달성 fragment
public class ChallengeFragment extends Fragment {

    private final static String TAG = "ChallengeFragment";

    private LinearLayout btnSelectMonth;
    private TextView tvSelectMonth;

    private LinearLayout llStart;
    private LinearLayout llRecord;
    private LinearLayout llRecommend;
    private LinearLayout llGood;

    private LayoutInflater inflater;
    private LocalDate selectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initField();
        setListener();
        setBadgeView();
    }

    private void initView(View v) {
        tvSelectMonth = v.findViewById(R.id.tv_select_month);
        btnSelectMonth = v.findViewById(R.id.ll_select_month);

        llStart = v.findViewById(R.id.ll_badge_start);
        llRecord = v.findViewById(R.id.ll_badge_record);
        llRecommend = v.findViewById(R.id.ll_badge_recommend);
        llGood = v.findViewById(R.id.ll_badge_good);
    }

    private void initField() {
        selectDate = LocalDate.now();
        tvSelectMonth.setText(Common.getMainLocalDateToString(selectDate));
    }

    private void setListener() {
        btnSelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 연도, 월 선택 다이얼로그 띄우기
                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(selectDate);
                yearMonthPickerDialog.show(getActivity().getSupportFragmentManager(), "YearMonthPickerDialog");

                yearMonthPickerDialog.setOnSelectListener(new YearMonthPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(LocalDate date) {
                        selectDate = date;
                        tvSelectMonth.setText(Common.getMainLocalDateToString(date));
                    }
                });
            }
        });
    }

    private void setBadgeView() {
        TypedArray badgeStartArray = getContext().getResources().obtainTypedArray(R.array.badgeStartArray);
        TypedArray badgeRecordArray = getContext().getResources().obtainTypedArray(R.array.badgeRecordArray);
        TypedArray badgeRecommendArray = getContext().getResources().obtainTypedArray(R.array.badgeRecommendArray);
        TypedArray badgeGoodArray = getContext().getResources().obtainTypedArray(R.array.badgeGoodArray);

        String[] strStartArray = getContext().getResources().getStringArray(R.array.strStartArray);
        String[] strRecordArray = getContext().getResources().getStringArray(R.array.strRecordArray);
        String[] strRecommendArray = getContext().getResources().getStringArray(R.array.strRecommendArray);
        String[] strGoodArray = getContext().getResources().getStringArray(R.array.strGoodArray);

        addBadgeView(badgeStartArray, strStartArray, llStart);
        addBadgeView(badgeRecordArray, strRecordArray, llRecord);
        addBadgeView(badgeRecommendArray, strRecommendArray, llRecommend);
        addBadgeView(badgeGoodArray, strGoodArray, llGood);
    }

    private void addBadgeView(TypedArray badgeArray, String[] titleArray, LinearLayout linearLayout) {
        for (int i = 0; i < badgeArray.length(); i++) {
            View view = inflater.inflate(R.layout.view_badge, null);
            ImageView ivBadge = view.findViewById(R.id.iv_badge);
            TextView tvBadgeName = view.findViewById(R.id.tv_badge_title);

            ivBadge.setImageResource(badgeArray.getResourceId(i, 0));
            tvBadgeName.setText(titleArray[i]);

            linearLayout.addView(view);
        }
    }
}
