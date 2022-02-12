package com.dnd.moneyroutine.service;

import androidx.annotation.NonNull;

import com.dnd.moneyroutine.custom.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 헤더에 토큰 추가할 때 사용되는 클래스
public class HeaderRetrofit {
    public static Retrofit getInstance(Interceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                .baseUrl(Constants.SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public Retrofit getTokenHeaderInstance(String token) {
        return getInstance(makeTokenHeaderInterceptor(token));
    }

    private Interceptor makeTokenHeaderInterceptor(String token) {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest;
                if (token != null && !token.equals("")) { // 토큰이 있는 경우
                    // Authorization 헤더에 jwt 토큰 추가
                    newRequest = chain.request().newBuilder().addHeader(Constants.tokenHeader, token).build();

                } else newRequest = chain.request();
                return chain.proceed(newRequest);
            }
        };
    }
}
