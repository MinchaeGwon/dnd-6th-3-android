package com.dnd.moneyroutine.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

// api interface
public interface RetrofitService {
    @GET(".")
    Call<JsonObject> test();
}
