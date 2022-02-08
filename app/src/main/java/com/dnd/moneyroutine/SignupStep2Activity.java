package com.dnd.moneyroutine;

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

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.UserForm;
import com.dnd.moneyroutine.service.RequestService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupStep2Activity extends AppCompatActivity {

    private final static String TAG = "SignupStep2";

    private ImageButton ibBack;
    private EditText etPassword;
    private EditText etPasswordRepeat;
    private TextView tvPasswordConfirm;
    private Button btnNext;

    private ConstraintLayout.LayoutParams contentLayoutParams;
    private InputMethodManager inputManager;
    private SoftKeyboardDetector softKeyboardDetector;

    private String email;
    private float scale;

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
        setContentView(R.layout.activity_signup_step2);

        initView();
        initField();
        setListener();
    }

    private void init() {

    }

    private void initView() {
        ibBack = findViewById(R.id.ib_sign2_back);
        etPassword = findViewById(R.id.et_signup2_password);
        etPasswordRepeat = findViewById(R.id.et_signup2_password_repeat);
        tvPasswordConfirm = findViewById(R.id.tv_password_confirm);
        btnNext = findViewById(R.id.btn_signup2_next);
    }

    private void initField() {
        contentLayoutParams = (ConstraintLayout.LayoutParams) btnNext.getLayoutParams();

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

        scale = getResources().getDisplayMetrics().density;
        email = getIntent().getStringExtra("email");
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
                if (etPassword.isFocused() || etPasswordRepeat.isFocused()) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                joinToServer(email, etPassword.getText().toString());
            }
        });

        // 키보드가 내려갈 때 수행
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                if (tvPasswordConfirm.getText().equals("비밀번호가 일치하지 않습니다")) {
                    btnNext.setBackgroundResource(R.drawable.rectangle_ced4da_radius_8);
                    btnNext.setEnabled(false);
                } else {
                    btnNext.setBackgroundResource(R.drawable.rectangle_343a40_radius_8);
                    btnNext.setEnabled(true);
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
                if (tvPasswordConfirm.getVisibility() == View.VISIBLE) {
                    btnNext.setBackgroundColor(Color.parseColor("#ced4da"));
                    btnNext.setEnabled(false);
                } else {
                    btnNext.setBackgroundColor(Color.parseColor("#343a40"));
                    btnNext.setEnabled(true);
                }

                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnNext.setLayoutParams(contentLayoutParams);
            }
        });

        //  키보드에서 완료 버튼 누르면 키보드 내리기
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etPassword.isFocused()) {
                            etPassword.clearFocus();
                        }

                        if (etPasswordRepeat.isFocused()) {
                            etPasswordRepeat.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        };

        etPassword.setOnEditorActionListener(onEditorActionListener);
        etPasswordRepeat.setOnEditorActionListener(onEditorActionListener);

        // focus 되어있는지 확인
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    view.setBackgroundResource(R.drawable.rectangle_495057_stroke_radius_8);
                } else {
                    view.setBackgroundResource(R.drawable.rectangle_f8f9fa_radius_8);
                }
            }
        };

        etPassword.setOnFocusChangeListener(onFocusChangeListener);
        etPasswordRepeat.setOnFocusChangeListener(onFocusChangeListener);

        // 비밀번호, 비밀번호 확인 일치 여부 확인, 다음 버튼 활성화/비활성화
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etPasswordRepeat.length() == 0) {
                    tvPasswordConfirm.setVisibility(View.INVISIBLE);

                    // 키보드가 활성화 되지 않은 경우에만 둥글게 표시
                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundColor(Color.parseColor("#ced4da"));
                    } else {
                        btnNext.setBackgroundResource(R.drawable.rectangle_ced4da_radius_8);
                    }

                    btnNext.setEnabled(false);
                } else {
                    if (etPasswordRepeat.getText().toString().equals(etPassword.getText().toString())) {
                        tvPasswordConfirm.setVisibility(View.INVISIBLE);

                        if (etPasswordRepeat.isFocused()) {
                            etPasswordRepeat.setBackgroundResource(R.drawable.rectangle_495057_stroke_radius_8);
                        } else {
                            etPasswordRepeat.setBackgroundResource(R.drawable.rectangle_f8f9fa_radius_8);
                        }

                        if (inputManager.isAcceptingText()) {
                            btnNext.setBackgroundColor(Color.parseColor("#343a40"));
                        } else {
                            btnNext.setBackgroundResource(R.drawable.rectangle_343a40_radius_8);
                        }

                        btnNext.setEnabled(true);
                    } else {
                        tvPasswordConfirm.setVisibility(View.VISIBLE);
                        etPasswordRepeat.setBackgroundResource(R.drawable.rectangle_fa5c6c_stroke_radius_8);

                        if (inputManager.isAcceptingText()) {
                            btnNext.setBackgroundColor(Color.parseColor("#ced4da"));
                        } else {
                            btnNext.setBackgroundResource(R.drawable.rectangle_ced4da_radius_8);
                        }

                        btnNext.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        etPassword.addTextChangedListener(textWatcher);
        etPasswordRepeat.addTextChangedListener(textWatcher);
    }

    // 회원 정보 서버로 전달, 회원가입 성공 여부 확인하여 토큰 값 SharedPreferences에 저장
    private void joinToServer(String email, String password) {
        Call<JsonObject> call = RequestService.getInstance().join(new UserForm(email, password));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

//                    if (responseJson.get("code").getAsInt() == 200 && responseJson.get("response").getAsBoolean()) {
//                        String token = responseJson.get("access_token").getAsString();
//                        saveTokenAndMoveActivity(token);
//                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(SignupStep2Activity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveTokenAndMoveActivity(String jwtToken) {
        PreferenceManager.setString(this, Constants.tokenKey, jwtToken);

        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 기존 화면 모두 clear
        startActivity(intent);
    }
}