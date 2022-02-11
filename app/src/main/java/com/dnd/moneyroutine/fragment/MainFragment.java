package com.dnd.moneyroutine.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.service.RequestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    private final static String TAG = "MainFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
