package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.custom.Common;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 서버 요청 관련 service class
public class RequestService {

    private static RequestService requestService = null;
    private Retrofit retrofit;

    private RetrofitService retrofitService;

    public RequestService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Common.SERVER)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofitService = retrofit.create(RetrofitService.class);
    }

    public static RequestService getInstance() {
        if (requestService == null) {
            requestService = new RequestService();
        }

        return requestService;
    }

    public Call<JsonObject> test() {
        return  retrofitService.test();
    }
}
