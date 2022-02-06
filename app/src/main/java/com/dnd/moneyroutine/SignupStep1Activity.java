package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupStep1Activity extends AppCompatActivity {

    private ImageButton ibBack;
    private EditText etEmail;
    private Button btnNext;

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
        setContentView(R.layout.activity_signup_step1);

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        init();
        setListener();
    }

    private void init() {
        ibBack = findViewById(R.id.ib_sign1_back);
        etEmail = findViewById(R.id.et_signup1_email);
        btnNext = findViewById(R.id.btn_signup1_next);
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
                    Intent intent = new Intent(SignupStep1Activity.this, SignupStep2Activity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupStep1Activity.this, "이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //  키보드에서 완료 버튼 누르면 키보드 내리기
        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etEmail.length() != 0) {
                    btnNext.setBackgroundResource(R.drawable.rectangle_black_212529_radius_8);
                    btnNext.setEnabled(true);
                } else {
                    btnNext.setBackgroundResource(R.drawable.rectangle_gray_e9ecef_radius_8);
                    btnNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
}