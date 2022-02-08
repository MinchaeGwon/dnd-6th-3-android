package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RequestService;
import com.dnd.moneyroutine.service.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
        setContentView(R.layout.activity_main);

        /*
         * 헤더에 토큰 추가하는 코드
         * String token = PreferenceManager.getToken(this, Constants.tokenKey); // 토큰 가져오기
         *
         * HeaderRetrofit headerRetrofit = new HeaderRetrofit();
         * Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
         * RetrofitService retrofitService = retrofit.create(RetrofitService.class);
         *
         * Call<String> call = retrofitService.test();
         */

        Call<String> call = RequestService.getInstance().test(); // 토큰이 필요 없을 때 사용하는 코드

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(MainActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}