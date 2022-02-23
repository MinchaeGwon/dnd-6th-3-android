package com.dnd.moneyroutine.service;

import com.dnd.moneyroutine.dto.BudgetDetailModel;
import com.dnd.moneyroutine.dto.CustomCategoryCreateDto;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.dto.UserForm;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// 서버 요청 관련 service class : 헤더에 토큰 필요 없을 때 사용
public class RequestService {

    private static RequestService requestService = null;

    private Retrofit retrofit;
    private RetrofitService retrofitService;

    public RequestService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitService = retrofit.create(RetrofitService.class);
    }

    public static RequestService getInstance() {
        if (requestService == null) {
            requestService = new RequestService();
        }

        return requestService;
    }

    public Call<String> test() {
        return retrofitService.test();
    }

    public Call<JsonObject> join(UserForm userForm) {
        return retrofitService.join(userForm);
    }

    public Call<JsonObject> login(UserForm userForm) {
        return retrofitService.login(userForm);
    }

    public Call<JsonObject> create(CustomCategoryCreateDto customCategoryCreateDto) { return retrofitService.create(customCategoryCreateDto); }

    public Call<BudgetDetailModel> goal(BudgetDetailModel budgetDetailModel){ return retrofitService.goal(budgetDetailModel);}


}
