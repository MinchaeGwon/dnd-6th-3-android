package com.dnd.moneyroutine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.TextView;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.service.JWTUtils;

import java.text.DecimalFormat;
import java.util.Calendar;

// 예산 수정 activity
public class BudgetUpdateActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private ImageButton ibCancel;

    private TextView tvMonth;
    private EditText etTotalBudget;
    private ImageView ivPencil;

    private TextView tvTotalBudget;
    private TextView tvRemainBudget;
    private RecyclerView rvCategory;

    private LinearLayout btnAddCat;
    private Button btnConfirm;
    private AlertDialog cancelDialog;

    private InputMethodManager inputManager;
    private SoftKeyboardDetector softKeyboardDetector;
    private ConstraintLayout.LayoutParams contentLayoutParams;
    private float scale;

    private DecimalFormat decimalFormat;
    private String result = "";

    private String token;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_update);

        initView();
        initField();

        setListener();
        setButtonSize();

        setBudgetInfo();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_update_budget_back);
        ibCancel = findViewById(R.id.ib_update_budget_cancel);

        tvMonth = findViewById(R.id.tv_update_budget_month);
        etTotalBudget = findViewById(R.id.et_update_budget_total);
        ivPencil = findViewById(R.id.iv_update_budget_pencil);

        tvTotalBudget = findViewById(R.id.tv_update_budget_total);
        tvRemainBudget = findViewById(R.id.tv_update_budget_remain);

        rvCategory = findViewById(R.id.rv_update_budget_cat);

        btnAddCat = findViewById(R.id.btn_update_budget_add_cat);
        btnConfirm = findViewById(R.id.btn_update_budget_confirm);
    }

    private void initField() {
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

        contentLayoutParams = (ConstraintLayout.LayoutParams) btnConfirm.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        decimalFormat = new DecimalFormat("#,###");

        token = PreferenceManager.getToken(this, Constants.tokenKey);
        userId = JWTUtils.getUserId(token);

        Calendar calendar = Calendar.getInstance();
        tvMonth.setText((calendar.get(Calendar.MONTH) - 1) + "월");
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

        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BudgetUpdateActivity.this, AddExpenseCatActivity.class));
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setEditListener();
    }

    // edit 관련 리스너 설정
    private void setEditListener() {
        etTotalBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    ivPencil.setImageResource(R.drawable.icon_pencil_green);
                } else {
                    ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                }
            }
        });

        etTotalBudget.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etTotalBudget.isFocused()) {
                            etTotalBudget.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        });

        etTotalBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tvRemainBudget.getText().toString().contains("남음")) {
                    etTotalBudget.setTextColor(Color.parseColor("#212529"));
                    tvRemainBudget.setTextColor(Color.parseColor("#212529"));
                } else if (tvRemainBudget.getText().toString().contains("초과")) {
                    etTotalBudget.setTextColor(Color.parseColor("#E70621"));
                    tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                }

                // 금액에 쉼표 추가
                if (etTotalBudget.isFocused() && !TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    etTotalBudget.setText(result);
                    etTotalBudget.setSelection(result.length());
                }

                if (etTotalBudget.length() > 0) {
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
                if (etTotalBudget.length() > 0) {
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

                if (etTotalBudget.length() > 0) {
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

    // 수정할 예산 정보 바인딩
    private void setBudgetInfo() {

    }

    private void setCancelDialog() {
        if (cancelDialog != null) return;
        makeCancelDialog();
    }

    // 예산 수정 종료 확인 다이얼로그 만들기
    private void makeCancelDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_cancel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        cancelDialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = view.findViewById(R.id.btn_dialog_cancel);

        tvTitle.setText("예산 수정을 종료할까요?");
        tvContent.setText("작성한 정보는 저장되지 않습니다");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
            }
        });
    }
}