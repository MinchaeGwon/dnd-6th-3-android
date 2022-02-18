package com.dnd.moneyroutine.custom;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.dnd.moneyroutine.R;

import java.util.ArrayList;

public class DayPickerDialog extends DialogFragment {
    public interface OnSelectListener {
        void onSelect(ArrayList<String> days);
    }

    private OnSelectListener onSelectListener;

    private Button btnConfirm;
    private Button btnCancel;

    private TextView tvMon;
    private TextView tvTue;
    private TextView tvWed;
    private TextView tvThur;
    private TextView tvFri;
    private TextView tvSat;
    private TextView tvSun;

    ArrayList<String> dayList;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public DayPickerDialog() {}

    public DayPickerDialog(ArrayList<String> dayList) {
        this.dayList = dayList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_day_picker, null);
        builder.setView(dialog);

        initView(dialog);
        setDayClickListener();
        setBtnClickListener();

        setSelectDayInfo();

        return builder.create();
    }

    private void initView(View v) {
        tvMon = v.findViewById(R.id.tv_mon);
        tvTue = v.findViewById(R.id.tv_tue);
        tvWed = v.findViewById(R.id.tv_wed);
        tvThur = v.findViewById(R.id.tv_thur);
        tvFri = v.findViewById(R.id.tv_fri);
        tvSat = v.findViewById(R.id.tv_sat);
        tvSun = v.findViewById(R.id.tv_sun);

        btnConfirm = v.findViewById(R.id.btn_dialog_confirm);
        btnCancel = v.findViewById(R.id.btn_dialog_cancel);
    }

    private void setDayClickListener() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) view;
                String selected = textView.getText().toString();

                // 선택된 경우
                if (textView.getCurrentTextColor() == Color.WHITE) {
                    textView.setBackgroundResource(R.drawable.button_day_unclicked);
                    textView.setTextColor(Color.parseColor("#ADB5BD"));

                    dayList.remove(selected);
                } else {
                    textView.setBackgroundResource(R.drawable.button_day_clicked);
                    textView.setTextColor(Color.WHITE);

                    dayList.add(selected);
                }
            }
        };

        tvMon.setOnClickListener(onClickListener);
        tvTue.setOnClickListener(onClickListener);
        tvWed.setOnClickListener(onClickListener);
        tvThur.setOnClickListener(onClickListener);
        tvFri.setOnClickListener(onClickListener);
        tvSat.setOnClickListener(onClickListener);
        tvSun.setOnClickListener(onClickListener);
    }

    // 반복 요일 정보 바인딩
    private void setSelectDayInfo() {
        TextView tv = new TextView(getContext());

        for (String day : dayList) {
            switch (day) {
                case "월":
                    tv = tvMon;
                    break;
                case "화":
                    tv = tvTue;
                    break;
                case "수":
                    tv = tvWed;
                    break;
                case "목":
                    tv = tvThur;
                    break;
                case "금":
                    tv = tvFri;
                    break;
                case "토":
                    tv = tvSat;
                    break;
                case "일":
                    tv = tvSun;
                    break;
            }

            tv.setBackgroundResource(R.drawable.button_day_clicked);
            tv.setTextColor(Color.WHITE);
        }
    }

    private void setBtnClickListener() {
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DayPickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(dayList);
                DayPickerDialog.this.getDialog().cancel();
            }
        });
    }
}
