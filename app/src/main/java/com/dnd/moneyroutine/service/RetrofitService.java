package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.dto.BudgetDetailModel;
import com.dnd.moneyroutine.dto.CustomCategoryModel;
import com.dnd.moneyroutine.dto.UserForm;
import com.dnd.moneyroutine.item.BudgetItem;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// api interface
public interface RetrofitService {
    @GET(".")
    Call<String> test();

    @POST("join")
    Call<JsonObject> join(@Body UserForm userForm);

    @POST("login")
    Call<JsonObject> login(@Body UserForm userForm);

    @POST("custom-category")
    Call<CustomCategoryModel> create(@Body CustomCategoryModel customCategoryModel);

    @POST("goal")
    Call<BudgetDetailModel> goal(@Body BudgetDetailModel budgetDetailModel);

    @GET("goal/info")
    Call<JsonObject> getMainGoalList(@Query("date") LocalDate date);

    @GET("expenditure/statics/weekly/{startDate}/{endDate}")
    Call<JsonObject> getWeeklyStatics(@Path("startDate") String startDate,
                                      @Path("endDate") String endDate);
}
