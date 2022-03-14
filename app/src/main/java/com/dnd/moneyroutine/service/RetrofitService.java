package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.dto.BasicCategoryForm;
import com.dnd.moneyroutine.dto.BudgetDetailModel;
import com.dnd.moneyroutine.dto.CustomCategoryCreateDto;
import com.dnd.moneyroutine.dto.DirectCustomCategoryForm;
import com.dnd.moneyroutine.dto.ExpenseForm;
import com.dnd.moneyroutine.dto.GoalCategoryForm;
import com.dnd.moneyroutine.dto.GoalTotalForm;
import com.dnd.moneyroutine.dto.UserForm;
import com.dnd.moneyroutine.enums.EmotionEnum;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// api interface
public interface RetrofitService {
    @POST("exist")
    Call<JsonObject> isExistEmail(@Body UserForm userForm);

    @POST("join")
    Call<JsonObject> join(@Body UserForm userForm);

    @POST("login")
    Call<JsonObject> login(@Body UserForm userForm);

    @POST("custom-category")
    Call<JsonObject> create(@Body CustomCategoryCreateDto customCategoryCreateDto);

    @POST("goal")
    Call<JsonObject> goal(@Body BudgetDetailModel budgetDetailModel);

    @GET("goal/check")
    Call<JsonObject> checkGoal();

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

    @GET("expenditure/statistics/monthly/detail/{startDate}/{endDate}/{categoryId}/{isCustom}")
    Call<JsonObject> getMonthlyDetail(@Path("startDate") LocalDate startDate,
                                      @Path("endDate") LocalDate endDate,
                                      @Path("categoryId") int categoryId,
                                      @Path("isCustom") boolean custom);

    @POST("goal/continue")
    Call<JsonObject> continueGoal();

    @GET("goal-category")
    Call<JsonObject> getGoalCategory();

    @DELETE("goal-category")
    Call<JsonObject> deleteGoalCategory(@Body int goalCategoryId);

    @PATCH("goal-category")
    Call<JsonObject> updateGoalCategory(@Body GoalCategoryForm goalCategoryForm);

    @POST("goal-category/custom")
    Call<JsonObject> directAddGoalCategory(@Body DirectCustomCategoryForm categoryForm);

    @POST("goal-category/pick")
    Call<JsonObject> addBasicGoalCategory(@Body BasicCategoryForm basicCategoryForm);

    @PATCH("goal/budget")
    Call<JsonObject> updateTotalBudget(@Body GoalTotalForm goalTotalForm);

    @POST("expenditure")
    Call<JsonObject> addExpenditure(@Body ExpenseForm expenseForm);

    @GET("category")
    Call<JsonObject> getCategoryList();

    @GET("category/except-list")
    Call<JsonObject> getExceptCategory(@Query("goalId") int goalId);

    @GET("diary/weekly")
    Call<JsonObject> getWeeklyDiary(@Query("year") int year, @Query("month") int month, @Query("week") int week);

    @GET("diary/daily")
    Call<JsonObject> getDailyDiary(@Query("date") LocalDate date);

    @GET("diary/monthly-best")
    Call<JsonObject> getMonthlyBestEmotion(@Query("year") int year, @Query("month") int month);

    @GET("diary/monthly")
    Call<JsonObject> getMonthlyDiaryByEmotion(@Query("year") int year, @Query("month") int month, @Query("emotion") EmotionEnum emotionEnum);

    @GET("logout")
    Call<JsonObject> logout();

    @DELETE("withdraw")
    Call<JsonObject> withdraw();
}
