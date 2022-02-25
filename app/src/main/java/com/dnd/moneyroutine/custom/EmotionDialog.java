package com.dnd.moneyroutine.custom;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.enums.EmotionEnum;

import java.time.LocalDate;

// 소비 감정 선택할 때 사용
public class EmotionDialog extends DialogFragment {

    public interface OnSelectListener {
        void onSelect(EmotionEnum emotion);
    }

    private OnSelectListener onSelectListener;

    private EmotionEnum emotion;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public EmotionDialog(EmotionEnum emotion) {
        this.emotion = emotion;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_emotion, null);
        builder.setView(dialog);

        Button btnConfirm = dialog.findViewById(R.id.btn_dialog_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(emotion);
                EmotionDialog.this.getDialog().cancel();
            }
        });

        RadioGroup rgEmotion = dialog.findViewById(R.id.rg_emotion);
        rgEmotion.check(mappingEmotionId());

        rgEmotion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.rb_emotion_good:
                        emotion = EmotionEnum.GOOD;
                        break;
                    case R.id.rb_emotion_soso:
                        emotion = EmotionEnum.SOSO;
                        break;
                    case R.id.rb_emotion_bad:
                        emotion = EmotionEnum.BAD;
                        break;
                }
            }
        });

        return builder.create();
    }

    private int mappingEmotionId() {
        switch (emotion) {
            case GOOD:
                return R.id.rb_emotion_good;
            case SOSO:
                return R.id.rb_emotion_soso;
            case BAD:
                return R.id.rb_emotion_bad;
        }
        return -1;
    }
}
