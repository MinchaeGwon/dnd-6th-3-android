package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.PreferenceManager;
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

    private InputMethodManager inputManager;
    private String email;

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

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        email = getIntent().getStringExtra("email");

        init();
        setListener();
    }

    private void init() {
        ibBack = findViewById(R.id.ib_sign2_back);
        etPassword = findViewById(R.id.et_signup2_password);
        etPasswordRepeat = findViewById(R.id.et_signup2_password_repeat);
        tvPasswordConfirm = findViewById(R.id.tv_password_confirm);
        btnNext = findViewById(R.id.btn_signup2_next);
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

        //  키보드에서 완료 버튼 누르면 키보드 내리기
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

        // 비밀번호, 비밀번호 확인 일치 여부 확인
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etPasswordRepeat.length() == 0) {
                    tvPasswordConfirm.setVisibility(View.INVISIBLE);

                    btnNext.setBackgroundResource(R.drawable.rectangle_gray_e9ecef_radius_8);
                    btnNext.setEnabled(false);
                } else {
                    tvPasswordConfirm.setVisibility(View.VISIBLE);

                    if (etPasswordRepeat.getText().toString().equals(etPassword.getText().toString())) {
                        tvPasswordConfirm.setText("비밀번호가 일치합니다");

                        btnNext.setBackgroundResource(R.drawable.rectangle_black_212529_radius_8);
                        btnNext.setEnabled(true);
                    } else {
                        tvPasswordConfirm.setText("비밀번호가 일치하지 않습니다");

                        btnNext.setBackgroundResource(R.drawable.rectangle_gray_e9ecef_radius_8);
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
//                        String token = responseJson.get("token").getAsString();
//                        String refreshToken = responseJson.get("refreshToken").getAsString();
//                        saveTokenAndMoveActivity(token, refreshToken);
//                    } else {
//                        // 회원가입 실패 다이얼로그 띄움
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

    public void saveTokenAndMoveActivity(String jwtToken, String refreshToken) {
        PreferenceManager.setString(this, (new Common()).getTokenKey(), jwtToken);
        PreferenceManager.setString(this, Common.REFRESH_TOKEN_KEY, refreshToken);

        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 기존 화면 모두 clear
        startActivity(intent);
    }
}