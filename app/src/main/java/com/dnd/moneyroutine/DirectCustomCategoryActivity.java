package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.DirectCustomCategoryForm;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 지출 분야 추가 - 직접 추가 activity
public class DirectCustomCategoryActivity extends AppCompatActivity {

    private static final String TAG = "DirectAddCustom";

    private ImageButton ibBack;
    private Button btnConfirm;

    private LinearLayout llCustomEmoji;
    private TextView tvEmoji;

    private LinearLayout llCategory;
    private EditText etCategoryName;
    private ImageView ivEraseName;

    private LinearLayout llBudget;
    private ImageView ivWon;
    private EditText etBudget;
    private TextView tvWon;

    private LinearLayout llDetail;
    private EditText etDetail;
    private ImageView ivEraseDetail;

    private ConstraintLayout bgBlack;

    private String token;
    private int goalId;
    private DirectCustomCategoryForm customCategoryForm;

    private InputMethodManager inputManager;
    private DecimalFormat decimalFormat;
    private String result = "";

    private ConstraintLayout.LayoutParams contentLayoutParams;
    private float scale;

    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_custom_category);

        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        decimalFormat = new DecimalFormat("#,###");

        token = PreferenceManager.getToken(this, Constants.tokenKey);
        goalId = getIntent().getIntExtra("goalId", -1);

        initView();
        setBtnListener();
        setEditFocusListener(); // 카테고리 이름, 예산, 설명 입력
        setEmojiListener(); // 이모지 선택
        setBtnSize();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_custom_back);
        btnConfirm = findViewById(R.id.btn_custom_confirm);

        llCustomEmoji = findViewById(R.id.ll_custom_emoji);
        tvEmoji = findViewById(R.id.tv_custom_emoji);

        llCategory = findViewById(R.id.ll_custom_category);
        etCategoryName = findViewById(R.id.et_custom_category);
        ivEraseName = findViewById(R.id.iv_custom_erase_category);

        llBudget = findViewById(R.id.ll_custom_money);
        ivWon = findViewById(R.id.iv_custom_won);
        etBudget = findViewById(R.id.et_custom_money);
        tvWon = findViewById(R.id.tv_custom_won);

        llDetail = findViewById(R.id.ll_custom_ex);
        etDetail = findViewById(R.id.et_custom_ex);
        ivEraseDetail = findViewById(R.id.iv_custom_erase_ex);

        bgBlack = findViewById(R.id.bg_black);
    }

    private void setBtnListener() {
        //이전 버튼
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 확인 버튼
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iconToByte = null;
                String name = etCategoryName.getText().toString();
                String ex = etDetail.getText().toString();
                String icon = tvEmoji.getText().toString();
                String budget = etBudget.getText().toString().replaceAll("\\,", "");

                Log.d(TAG, "목표 id : " + goalId);

                customCategoryForm = new DirectCustomCategoryForm();

                customCategoryForm.setGoalId(goalId);
                customCategoryForm.setName(name);
                customCategoryForm.setDetail(ex);
                customCategoryForm.setBudget(Integer.parseInt(budget));

                try {
                    iconToByte = URLEncoder.encode(icon, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                customCategoryForm.setEmoji(iconToByte);

                directAddCustomCategoryServer();
            }
        });
    }

    // edittext 디자인 변경
    private void setEditFocusListener() {
        // 이름 입력창 누르면 background 변경
       etCategoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    llCategory.setBackgroundResource(R.drawable.textbox_typing);
                    ivEraseName.setVisibility(View.VISIBLE);

                    // x 이미지 누르면 edittext 비움
                    ivEraseName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etCategoryName.setText("");
                        }
                    });
                } else {
                    llCategory.setBackgroundResource(R.drawable.textbox_default);
                    ivEraseName.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 금액 입력창이 눌리면 입력창 background 변경
        etBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    llBudget.setBackgroundResource(R.drawable.textbox_typing);
                    ivWon.setImageResource(R.drawable.won_black);
                    tvWon.setTextColor(Color.parseColor("#495057"));
                } else {
                    llBudget.setBackgroundResource(R.drawable.textbox_default);
                    ivWon.setImageResource(R.drawable.won_gray);
                    tvWon.setTextColor(Color.parseColor("#ADB5BD"));
                }
            }
        });

        // 설명 입력창 누르면 디자인 변경
        etDetail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    llDetail.setBackgroundResource(R.drawable.textbox_typing);
                    ivEraseDetail.setVisibility(View.VISIBLE);

                    // x 이미지 누르면 edittext 비움
                    ivEraseDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etDetail.setText("");
                        }
                    });
                } else {
                    llDetail.setBackgroundResource(R.drawable.textbox_default);
                    ivEraseDetail.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // 확인 버튼 사이즈 조절
    private void setBtnSize() {
        SoftKeyboardDetector softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));
        contentLayoutParams = (ConstraintLayout.LayoutParams) btnConfirm.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        bgBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bgBlack.setVisibility(View.GONE);
                inputManager.hideSoftInputFromWindow(DirectCustomCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        //키보드 내려갔을 때
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                getCurrentFocus().clearFocus();

                //키보드 내려가면 이모지 선택시 어두워졌던 배경 다시 원래대로
                bgBlack.setVisibility(View.GONE);

                if (btnConfirm.isEnabled()) {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
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
                if (btnConfirm.isEnabled()) {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                }

                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 금액에 쉼표 추가
                if (etBudget.isFocused() && !TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    etBudget.setText(result);
                    etBudget.setSelection(result.length());
                }

                // 모두 입력되어야만 버튼 enable
                if (etCategoryName.length() > 0 && etBudget.length() > 0 && etDetail.length() > 0) {
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
        };

        etCategoryName.addTextChangedListener(textWatcher);
        etBudget.addTextChangedListener(textWatcher);
        etDetail.addTextChangedListener(textWatcher);

        //  키보드에서 완료 버튼 누르면 키보드 내리기
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etCategoryName.isFocused()) {
                            etCategoryName.clearFocus();
                        }

                        if (etBudget.isFocused()) {
                            etBudget.clearFocus();
                        }

                        if (etDetail.isFocused()) {
                            etDetail.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        };

        etCategoryName.setOnEditorActionListener(onEditorActionListener);
        etBudget.setOnEditorActionListener(onEditorActionListener);
        etDetail.setOnEditorActionListener(onEditorActionListener);
    }

    // 이모지 편집
    private void setEmojiListener() {
        EditText etEmoji = findViewById(R.id.et_custom_emoji);

        llCustomEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이모지 편집 누르면 키보드 띄우고 배경 어둡게
                etEmoji.requestFocus();
                etEmoji.setInputType(1);
                etEmoji.setCursorVisible(true);

                bgBlack.setVisibility(View.VISIBLE);
                inputManager.showSoftInput(etEmoji, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        etEmoji.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etEmoji.length() > 0) {
                    flag = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력된 값이 이모지라면 이모지 부분 textview에 선택한 값 띄움
                if (flag) {
                    flag = false;

                    if (isEmoji(etEmoji.getText().toString())) {
                        if (tvEmoji.getText() != etEmoji.getText()) {
                            tvEmoji.setText(etEmoji.getText());
                        }

                        inputManager.hideSoftInputFromWindow(DirectCustomCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        bgBlack.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(DirectCustomCategoryActivity.this, "이모지만 입력 가능합니다", Toast.LENGTH_SHORT).show();
                    }

                    etEmoji.setText("");
                }
            }
        });
    }

    // 이모지가 입력되었는지 확인
    private static boolean isEmoji(String message) {
        Pattern rex = Pattern.compile("[\\x{10000}-\\x{10ffff}\ud800-\udfff]");
        Matcher matcher = rex.matcher(message);

        return matcher.matches();
    }

    // 사용자 입력 카테고리 추가하기
    private void directAddCustomCategoryServer() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.directAddGoalCategory(customCategoryForm);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (responseJson.get("data").getAsBoolean()) {
                            Intent intent = new Intent(DirectCustomCategoryActivity.this, BudgetUpdateActivity.class);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DirectCustomCategoryActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}