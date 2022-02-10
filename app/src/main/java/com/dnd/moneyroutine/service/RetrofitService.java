package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.CustomCategoryModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// api interface
public interface RetrofitService {
    @GET(".")
    Call<String> test();

    @POST("custom-category")
    Call<CustomCategoryModel> create (@Body CustomCategoryModel customCategoryModel);
}
