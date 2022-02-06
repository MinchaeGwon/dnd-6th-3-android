package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.dto.UserForm;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

// api interface
public interface RetrofitService {
    @GET(".")
    Call<String> test();

    @POST("join")
    Call<JsonObject> join(@Body UserForm userForm);

    @POST("login")
    Call<JsonObject> login(@Body UserForm userForm);
}
