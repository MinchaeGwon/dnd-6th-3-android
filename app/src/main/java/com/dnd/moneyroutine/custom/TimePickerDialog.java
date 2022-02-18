package com.dnd.moneyroutine.custom;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.dnd.moneyroutine.R;

import java.time.LocalDate;

// 시간 선택할 때 사용
public class TimePickerDialog extends DialogFragment {

    public interface OnSelectListener {
        void onSelect(int hour, int minute);
    }

    private OnSelectListener onSelectListener;

    private int hour;
    private int minute;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public TimePickerDialog() {}

    public TimePickerDialog(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_time_picker, null);
        builder.setView(dialog);

        Button btnConfirm = dialog.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);

        TimePicker timePicker = dialog.findViewById(R.id.tp_time);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TimePickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(timePicker.getHour(), timePicker.getMinute());
                TimePickerDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
