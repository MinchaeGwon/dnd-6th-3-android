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
import java.util.Calendar;

// 연도 선택할 때 사용
public class WeekPickerDialog extends DialogFragment {

    public interface OnSelectListener {
        void onSelect(Calendar calendar);
    }

    private static final int MIN_YEAR = 2000;

    private OnSelectListener onSelectListener;
    private Calendar calendar;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public WeekPickerDialog() {}

    public WeekPickerDialog(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_week_picker, null);

        Button btnConfirm = dialog.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);

        NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);
        NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        NumberPicker weekPicker = dialog.findViewById(R.id.picker_week);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                WeekPickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.YEAR, yearPicker.getValue());
                calendar.set(Calendar.MONTH, monthPicker.getValue() - 1);

                if (weekPicker.getValue() == 1) {
                    calendar.set(Calendar.DATE, 1);
                } else if (weekPicker.getValue() == calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
                    calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                } else {
                    calendar.set(Calendar.WEEK_OF_MONTH, weekPicker.getValue());
                }

                calendar.getTime();

                onSelectListener.onSelect(calendar);
                WeekPickerDialog.this.getDialog().cancel();
            }
        });

        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int curMonth = today.get(Calendar.MONTH) + 1;
        int curMaxWeek = today.getActualMaximum(Calendar.WEEK_OF_MONTH);

        if (calendar.get(Calendar.DATE) > 1) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int maxWeek = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);

        yearPicker.setMinValue(MIN_YEAR);
        monthPicker.setMinValue(1);
        weekPicker.setMinValue(1);

        yearPicker.setMaxValue(curYear);

        if (year == curYear) {
            monthPicker.setMaxValue(curMonth);

            if (month == curMonth) {
                weekPicker.setMaxValue(curMaxWeek);
            } else {
                weekPicker.setMaxValue(maxWeek);
            }
        } else {
            monthPicker.setMaxValue(12);
            weekPicker.setMaxValue(maxWeek);
        }

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (numberPicker.getValue() == curYear) {
                    monthPicker.setMaxValue(curMonth);
                } else {
                    monthPicker.setMaxValue(12); // 12월까지 선택 가능
                }

                if (monthPicker.getValue() == curMonth) {
                    weekPicker.setMaxValue(curMaxWeek);
                } else {
                    Calendar temp = Calendar.getInstance();
                    temp.set(curYear, curMonth - 1, 1);

                    weekPicker.setMaxValue(temp.getActualMaximum(Calendar.WEEK_OF_MONTH));
                }
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (numberPicker.getValue() == curMonth) {
                    weekPicker.setMaxValue(curMaxWeek);
                } else {
                    Calendar temp = Calendar.getInstance();
                    temp.set(year, numberPicker.getValue() - 1, 1);

                    weekPicker.setMaxValue(temp.getActualMaximum(Calendar.WEEK_OF_MONTH));
                }
            }
        });

        yearPicker.setValue(year);
        monthPicker.setValue(month);
        weekPicker.setValue(week);

        builder.setView(dialog);

        return builder.create();
    }
}
