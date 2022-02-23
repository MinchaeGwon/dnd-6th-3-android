package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.dto.BudgetDetailModel;
import com.dnd.moneyroutine.dto.CustomCategoryCreateDto;
import com.dnd.moneyroutine.dto.UserForm;
import com.google.gson.JsonObject;

import java.time.LocalDate;

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
    Call<JsonObject> create(@Body CustomCategoryCreateDto customCategoryCreateDto);

    @POST("goal")
    Call<BudgetDetailModel> goal(@Body BudgetDetailModel budgetDetailModel);

    @GET("goal/info")
    Call<JsonObject> getMainGoalList(@Query("date") LocalDate date);

    @GET("expenditure/statistics/weekly/{startDate}/{endDate}")
    Call<JsonObject> getWeeklyStatistics(@Path("startDate") LocalDate startDate,
                                         @Path("endDate") LocalDate endDate);

    @GET("expenditure/weekly/{currentDate}")
    Call<JsonObject> getWeeklyTrend(@Path("currentDate") LocalDate currentDate);

    @GET("expenditure/statistics/monthly/{startDate}/{endDate}")
    Call<JsonObject> getMonthlyStatistics(@Path("startDate") LocalDate startDate,
                                          @Path("endDate") LocalDate endDate);

    @GET("expenditure/monthly/{currentDate}")
    Call<JsonObject> getMonthlyTrend(@Path("currentDate") LocalDate currentDate);

    @POST("goal/continue")
    Call<JsonObject> continueGoal();

    @GET("goal-category/")
    Call<JsonObject> getGoalCategory();
}
