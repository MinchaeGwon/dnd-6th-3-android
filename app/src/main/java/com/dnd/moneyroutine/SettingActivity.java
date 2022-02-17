package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;

// 설정 activity
public class SettingActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private LinearLayout btnNoti;
    private LinearLayout btnLogout;
    private LinearLayout btnWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        setListener();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_setting_back);
        btnNoti = findViewById(R.id.ll_setting_noti);
        btnLogout = findViewById(R.id.ll_setting_logout);
        btnWithdraw = findViewById(R.id.ll_setting_withdraw);
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutToServer();
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    // 로그아웃 하기
    private void logoutToServer() {
        // 서버 구현 전이기 때문에 SharedPreferences에서만 토큰을 삭제
        removeSharedPreferences();
        afterLogoutMoveActivity();

//        String token = PreferenceManager.getToken(getContext(), Constants.tokenKey); // 토큰 가져오기
//
//        // 헤더에 토큰 추가하는 코드
//        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
//        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
//        RetrofitService retroService = retrofit.create(RetrofitService.class);
//
//        Call<JsonObject> call = retroService.signout();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if(response.isSuccessful()) {
//                    // sharedPreferences가 보관하고 있던 토큰을 삭제
//                    removeSharedPreferences();
//                    afterLogoutMoveActivity();
//                    Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // SharedPreferences에서 토큰 제거
    private void removeSharedPreferences() {
        PreferenceManager.removeKey(this, Constants.tokenKey);
    }

    // 로그아웃 후에 어디로 이동할지 결정하는 메소드 -> SocialLoginActivity로 이동
    private void afterLogoutMoveActivity() {
        Intent intent = new Intent(this, SocialLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}