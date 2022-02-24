package com.dnd.moneyroutine;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.UserForm;
import com.dnd.moneyroutine.service.RequestService;
import com.google.gson.JsonObject;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NormalLoginActivity extends AppCompatActivity {

    private final static String TAG = "NormalLogin";

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvJoin;

    private AlertDialog failDialog;

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
        tvJoin = findViewById(R.id.tv_join);
    }

    private void setListener() {
        //  키보드에서 완료 버튼 누르면 키보드 내리기
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etEmail.isFocused()) {
                            etEmail.clearFocus();
                        }

                        if (etPassword.isFocused()) {
                            etPassword.clearFocus();
                        }
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

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    view.setBackgroundResource(R.drawable.textbox_typing);
                } else {
                    view.setBackgroundResource(R.drawable.textbox_default);
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    view.setBackgroundResource(R.drawable.textbox_typing);
                } else {
                    view.setBackgroundResource(R.drawable.textbox_default);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드 내리기
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (etEmail.isFocused()) {
                    etEmail.clearFocus();
                }

                if (etPassword.isFocused()) {
                    etPassword.clearFocus();
                }

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.length() > 0 && password.length() > 0) {
                    loginToServer(email, password);
                }
            }
        });

        tvJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NormalLoginActivity.this, SignupStep1Activity.class));
            }
        });
    }

    // 로그인 정보 서버로 전달, 로그인 성공 여부 확인하여 토큰 값 SharedPreferences에 저장
    private void loginToServer(String email, String password) {
        Call<JsonObject> call = RequestService.getInstance().login(new UserForm(email, password));
        call.enqueue(new Callback<JsonObject>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        String message = responseJson.get("responseMessage").getAsString();

                        if (message.equals("로그인 실패(존재하지 않는 이메일, 잘못된 비밀번호)")) {
                            setFailDialog();
                            failDialog.show();
                        } else {
                            JsonObject data = responseJson.get("data").getAsJsonObject();

                            String token = data.get("accessToken").getAsString();
                            String refreshToken = data.get("refreshToken").getAsString();

                            saveTokenAndMoveActivity(token, refreshToken);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(NormalLoginActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFailDialog() {
        if (failDialog != null) return;
        makeFailDialog();
    }

    // 로그인 실패 다이얼로그 만들기
    private void makeFailDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        failDialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);

        tvTitle.setText("로그인 실패");
        tvContent.setText("이메일 또는 비밀번호가 일치하지 않습니다");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failDialog.dismiss();
            }
        });
    }

    // 로그인 성공시 토큰 저장, 화면 이동
    private void saveTokenAndMoveActivity(String jwtToken, String refreshToken) {
        PreferenceManager.setString(this, Constants.tokenKey, jwtToken);
        PreferenceManager.setString(this, Constants.REFRESH_TOKEN_KEY, refreshToken);

        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 기존 화면 모두 clear
        startActivity(intent);
    }
}