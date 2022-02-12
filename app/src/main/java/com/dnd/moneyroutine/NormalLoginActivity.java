package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

public class NormalLoginActivity extends AppCompatActivity {

    private final static String TAG = "NormalLogin";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    private InputMethodManager inputManager;

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
        setContentView(R.layout.activity_normal_login);

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        init();
        setListener();
    }

    private void init() {
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login_email);
    }

    private void setListener() {
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

        etEmail.setOnEditorActionListener(onEditorActionListener);
        etPassword.setOnEditorActionListener(onEditorActionListener);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드 내리기
                if (etEmail.isFocused() || etPassword.isFocused()) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.length() != 0 && password.length() != 0) {
                    loginToServer(email, password);
                }
            }
        });
    }

 //    로그인 정보 서버로 전달, 로그인 성공 여부 확인하여 토큰 값 SharedPreferences에 저장
    private void loginToServer(String email, String password) {
        Call<JsonObject> call = RequestService.getInstance().login(new UserForm(email, password));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

//                    if (responseJson.get("code").getAsInt() == 200 && responseJson.get("response").getAsBoolean()) {
//                        String token = responseJson.get("access_token").getAsString();
//                        saveTokenAndMoveActivity(token);
//                    } else {
//                        // 로그인 정보가 맞지 않으면 로그인 실패 다이얼로그 띄움
//                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(NormalLoginActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokenAndMoveActivity(String jwtToken) {
        PreferenceManager.setString(this, Constants.tokenKey, jwtToken);

        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 기존 화면 모두 clear
        startActivity(intent);
    }
}