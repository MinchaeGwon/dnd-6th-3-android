package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GoalInfo {
    private int totalBudget;
    private int remainder;

    @SerializedName("goalCategoryDetailDtoList")
    private ArrayList<GoalCategoryDetail> goalCategoryDetailList;
}
