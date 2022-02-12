package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.dto.CustomCategoryModel;
import com.dnd.moneyroutine.dto.UserForm;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @POST("custom-category")
    Call<CustomCategoryModel> create (@Body CustomCategoryModel customCategoryModel);
}
