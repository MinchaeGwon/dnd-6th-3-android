package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SocialLoginActivity extends AppCompatActivity {

    private TextView tvEmailLogin; // 이메일 로그인 버튼
    private TextView tvEmailSignup; // 이메일 회원가입 버튼

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
        setContentView(R.layout.activity_social_login);

        init();
        setListener();
    }

    // UI 초기화
    private void init() {
        tvEmailLogin = findViewById(R.id.tv_email_login);
        tvEmailSignup = findViewById(R.id.tv_email_signup);
    }

    // 리스너 설정
    private void setListener() {
        // 이메일 로그인 페이지로 이동
        tvEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SocialLoginActivity.this, NormalLoginActivity.class));
            }
        });

        // 이메일 회원가입 페이지로 이동
        tvEmailSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SocialLoginActivity.this, SignupStep1Activity.class));
            }
        });
    }
}