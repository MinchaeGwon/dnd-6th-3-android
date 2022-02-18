package com.dnd.moneyroutine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.ExpenseForm;

// 소비 감정 입력 activity
public class AddFeelingActivity extends AppCompatActivity {

    private static final String TAG = "AddFeelingActivity";

    private ImageButton ibBack;
    private ImageButton ibCancel;

    private LinearLayout llContent;
    private ImageView ivPencil;
    private EditText etContent;

    private RadioGroup rgFeeling;
    private RadioButton rbGood;
    private RadioButton rbSoso;
    private RadioButton rbBad;

    private RadioButton selectFeeling;

    private Button btnConfirm;
    private AlertDialog cancelDialog;

    private InputMethodManager inputManager;
    private SoftKeyboardDetector softKeyboardDetector;
    private ConstraintLayout.LayoutParams contentLayoutParams;
    private float scale;

    private ExpenseForm expenseForm;

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
        setContentView(R.layout.activity_add_feeling);

        initView();
        initField();
        setListener();
        setButtonSize();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_add_feeling_back);
        ibCancel = findViewById(R.id.ib_add_feeling_cancel);

        llContent = findViewById(R.id.ll_feeling_content);
        ivPencil = findViewById(R.id.iv_feeling_pencil);
        etContent = findViewById(R.id.et_feeling_content);

        rgFeeling = findViewById(R.id.rg_feeling);
        rbGood = findViewById(R.id.rb_feeling_good);
        rbSoso = findViewById(R.id.rb_feeling_soso);
        rbBad = findViewById(R.id.rb_feeling_bad);

        btnConfirm = findViewById(R.id.btn_add_feeling_confirm);
    }

    private void initField() {
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

        contentLayoutParams = (ConstraintLayout.LayoutParams) btnConfirm.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        expenseForm = (ExpenseForm) getIntent().getSerializableExtra("expenseForm");
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelDialog();
                cancelDialog.show();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                expenseForm.setEmotion(mappingFeeling());
                expenseForm.setEmotionDetail(etContent.getText().toString());

                Log.d(TAG, "emotion : " + expenseForm.getEmotion());

                addExpense();
            }
        });

        rgFeeling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                switch (i) {
                    case R.id.rb_feeling_good:
                        selectFeeling = rbGood;
                        break;
                    case R.id.rb_feeling_soso:
                        selectFeeling = rbSoso;
                        break;
                    case R.id.rb_feeling_bad:
                        selectFeeling = rbBad;
                        break;
                }
            }
        });

        setEtListener();
    }

    private void setEtListener() {
        //  키보드에서 완료 버튼 누르면 키보드 내리기
        etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etContent.isFocused()) {
                            etContent.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        });

        // 내용 입력 창이 눌리면 입력창 background 변경
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    llContent.setBackgroundResource(R.drawable.textbox_typing);
                    ivPencil.setImageResource(R.drawable.icon_pencil_black);
                } else {
                    llContent.setBackgroundResource(R.drawable.textbox_default);
                    ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                }
            }
        });

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etContent.length() > 0 && selectFeeling != null) {
                    btnConfirm.setEnabled(true);

                    if (inputManager.isAcceptingText()) {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                    } else {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                    }
                } else {
                    btnConfirm.setEnabled(false);

                    if (inputManager.isAcceptingText()) {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                    } else {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setButtonSize() {
        //키보드 내려갔을 때
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                if (etContent.length() > 0 && selectFeeling != null) {
                    btnConfirm.setEnabled(true);
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
                    btnConfirm.setEnabled(false);
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);
                }

                contentLayoutParams.setMarginStart((int) (16 * scale + 0.2f));
                contentLayoutParams.setMarginEnd((int) (16 * scale + 0.2f));
                contentLayoutParams.bottomMargin = (int) (56 * scale + 0.2f);
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });

        // 키보드가 올라왔을 때
        softKeyboardDetector.setOnShownKeyboard(new SoftKeyboardDetector.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {

                if (etContent.length() > 0 && selectFeeling != null) {
                    btnConfirm.setEnabled(true);
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
                    btnConfirm.setEnabled(false);
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                }

                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });
    }

    // 지출 내역 추가
    private void addExpense() {
//        moveActivity();
    }

    private String mappingFeeling() {
        switch (rgFeeling.getCheckedRadioButtonId()) {
            case R.id.rb_feeling_good:
                return "GOOD";
            case R.id.rb_feeling_soso:
                return "SOSO";
            case R.id.rb_feeling_bad:
                return "BAD";
        }
        return null;
    }

    private void setCancelDialog() {
        if (cancelDialog != null) return;
        makeCancelDialog();
    }

    // 기록 종료 확인 다이얼로그 만들기
    private void makeCancelDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_cancel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        cancelDialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = view.findViewById(R.id.btn_dialog_cancel);

        tvTitle.setText("기록을 종료할까요?");
        tvContent.setText("작성한 내용은 저장되지 않습니다");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
                moveActivity();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
            }
        });
    }

    private void moveActivity() {
        Intent intent = new Intent(AddFeelingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 기존 화면 모두 clear
        startActivity(intent);
    }
}