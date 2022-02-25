package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.internal.http2.ErrorCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 설정 activity
public class SettingActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private LinearLayout btnNoti;
    private LinearLayout btnLogout;
    private LinearLayout btnWithdraw;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        token = PreferenceManager.getToken(this, Constants.tokenKey); // 토큰 가져오기

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
                Log.d("SettingActivity", "111111111");
                withdrawToServer();
            }
        });
    }

    // 로그아웃 하기
    private void logoutToServer() {
        // 서버 구현 전이기 때문에 sharedPreferences에서만 토큰 삭제
        removeSharedPreferences();
        afterMoveActivity();

//        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
//        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
//        RetrofitService retroService = retrofit.create(RetrofitService.class);
//
//        Call<JsonObject> call = retroService.logout();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    JsonObject responseJson = response.body();
//
//                    Log.d("SettingActivity", responseJson.toString());
//
//                    if (responseJson.get("statusCode").getAsInt() == 200) {
//                        // sharedPreferences가 보관하고 있던 토큰을 삭제
//                        removeSharedPreferences();
//                        afterMoveActivity();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(SettingActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // 서비스 탈퇴 하기
    private void withdrawToServer() {
        Log.d("SettingActivity", "222222");
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

//        Call<JsonObject> call = retroService.withdraw();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    JsonObject responseJson = response.body();
//
//                    Log.d("SettingActivity", responseJson.toString());
//
//                    if (responseJson.get("statusCode").getAsInt() == 200) {
//                        // sharedPreferences가 보관하고 있던 토큰을 삭제
//                        removeSharedPreferences();
//                        afterMoveActivity();
//                    }
//                } else {
////                    Log.d("SettingActivity", response.);
//                    Log.d("SettingActivity", "3333333333333");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(SettingActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // SharedPreferences에서 토큰 제거
    private void removeSharedPreferences() {
        PreferenceManager.removeKey(this, Constants.tokenKey);
        PreferenceManager.removeKey(this, Constants.REFRESH_TOKEN_KEY);
    }

    // 어디로 이동할지 결정하는 메소드 -> SocialLoginActivity로 이동
    private void afterMoveActivity() {
        Intent intent = new Intent(this, SocialLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public class MyErrorMessage {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}