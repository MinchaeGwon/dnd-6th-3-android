package com.dnd.moneyroutine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.UserForm;
import com.dnd.moneyroutine.service.RequestService;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupStep1Activity extends AppCompatActivity {

    private static final String TAG = "SignStep1Activity";

    private ImageButton ibBack;
    private EditText etEmail;
    private Button btnNext;
    private ImageButton ibRemove;

    private AlertDialog existDialog;

    private ConstraintLayout.LayoutParams contentLayoutParams;
    private InputMethodManager inputManager;
    private SoftKeyboardDetector softKeyboardDetector;
    private float scale;

    private UserForm userForm;

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
        setContentView(R.layout.activity_signup_step1);

        initView();
        initField();
        setListener();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_sign1_back);
        etEmail = findViewById(R.id.et_signup1_email);
        btnNext = findViewById(R.id.btn_signup1_next);
        ibRemove = findViewById(R.id.ib_sign1_email_remove);

        userForm = new UserForm();
    }

    private void initField() {
        contentLayoutParams = (ConstraintLayout.LayoutParams) btnNext.getLayoutParams();

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

        scale = getResources().getDisplayMetrics().density;
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드 내리기
                if (etEmail.isFocused()) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                String email = etEmail.getText().toString();

                if (isValidEmail(email)) {
                    userForm.setEmail(email);
                    checkExistEmail();
                } else {
                    Toast.makeText(SignupStep1Activity.this, "이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etEmail.setText("");
            }
        });

        // 키보드가 내려갈 때 수행
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                getCurrentFocus().clearFocus();

                if (btnNext.isEnabled()) {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_false);
                }

                contentLayoutParams.setMarginStart((int) (16 * scale + 0.2f));
                contentLayoutParams.setMarginEnd((int) (16 * scale + 0.2f));
                contentLayoutParams.bottomMargin = (int) (56 * scale + 0.2f);
                btnNext.setLayoutParams(contentLayoutParams);
            }
        });

        // 키보드가 올라올 때 수행
        softKeyboardDetector.setOnShownKeyboard(new SoftKeyboardDetector.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                if (btnNext.isEnabled()) {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                }

                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnNext.setLayoutParams(contentLayoutParams);
            }
        });

        //  키보드에서 완료 버튼 누르면 키보드 내리기
        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etEmail.isFocused()) {
                            etEmail.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        });

        // 다음 버튼 활성화/비활성화
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etEmail.length() == 0) {
                    ibRemove.setVisibility(View.INVISIBLE);

                    // 키보드가 활성화 되지 않은 경우에만 둥글게 표시
                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundColor(Color.parseColor("#ADB5BD"));
                    } else {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_false);
                    }

                    btnNext.setEnabled(false);
                } else {
                    ibRemove.setVisibility(View.VISIBLE);

                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundColor(Color.parseColor("#343a40"));
                    } else {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                    }

                    btnNext.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // focus 되어있는지 확인
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    view.setBackgroundResource(R.drawable.textbox_typing);

                    if (etEmail.length() != 0) {
                        ibRemove.setVisibility(View.VISIBLE);
                    }
                } else {
                    view.setBackgroundResource(R.drawable.textbox_default);
                    ibRemove.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // 이메일 validation
    private boolean isValidEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    private void checkExistEmail() {
        Call<JsonObject> call = RequestService.getInstance().isExistEmail(userForm);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        JsonObject data = responseJson.get("data").getAsJsonObject();

                        boolean exist = data.get("exist").getAsBoolean();

                        if (exist) {
                            setExistDialog();
                            existDialog.show();
                        } else {
                            Intent intent = new Intent(SignupStep1Activity.this, SignupStep2Activity.class);
                            intent.putExtra("userForm", userForm);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(SignupStep1Activity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setExistDialog() {
        if (existDialog != null) return;
        makeExistDialog();
    }

    // 이메일 중복 확인 다이얼로그 만들기
    private void makeExistDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        existDialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);

        tvTitle.setText("이미 존재하는 이메일입니다");
        tvContent.setText("새로운 이메일을 입력해주세요!");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                existDialog.dismiss();
            }
        });
    }
}