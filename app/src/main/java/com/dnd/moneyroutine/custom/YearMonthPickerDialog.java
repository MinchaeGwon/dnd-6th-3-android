package com.dnd.moneyroutine.custom;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.dnd.moneyroutine.R;

import java.time.LocalDate;

// 연도, 월 선택할 때 사용
public class YearMonthPickerDialog extends DialogFragment {

    public interface OnSelectListener {
        void onSelect(LocalDate date);
    }

    private static final int MIN_YEAR = 2000;

    private OnSelectListener onSelectListener;
    private LocalDate date;
    private boolean diary = false;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public YearMonthPickerDialog() {}

    public YearMonthPickerDialog(LocalDate date) {
        this.date = date;
    }

    public YearMonthPickerDialog(LocalDate date, boolean diary) {
        this.date = date;
        this.diary = diary;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_year_month_picker, null);

        Button btnConfirm = dialog.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);

        NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);
        NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LocalDate selectDate = LocalDate.of(yearPicker.getValue(), monthPicker.getValue(), 1);

                onSelectListener.onSelect(selectDate);
                YearMonthPickerDialog.this.getDialog().cancel();
            }
        });

        LocalDate curDate = LocalDate.now();

        if (!diary) {
            curDate = curDate.minusMonths(1);
        }

        int curYear = curDate.getYear();
        int curMonth = curDate.getMonthValue();

        int year = date.getYear();
        int month = date.getMonthValue();

        yearPicker.setMinValue(MIN_YEAR);
        monthPicker.setMinValue(1);

        yearPicker.setMaxValue(curYear);

        if (year != curYear) {
            monthPicker.setMaxValue(12);
        } else {
            monthPicker.setMaxValue(curMonth);
        }

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (numberPicker.getValue() == curYear) {
                    monthPicker.setMaxValue(curMonth);
                } else {
                    monthPicker.setMaxValue(12); // 12월까지 선택 가능
                }
            }
        });

        yearPicker.setValue(year);
        monthPicker.setValue(month);

        builder.setView(dialog);

        return builder.create();
    }
}
