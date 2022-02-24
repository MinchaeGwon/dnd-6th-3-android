package com.dnd.moneyroutine.dto;

import com.dnd.moneyroutine.enums.GoalStateEnum;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoalInfo implements Serializable {
    private int goalId;
    private GoalStateEnum goalState;
    private int remainder;
    private int totalBudget;

    @SerializedName("goalCategoryDetailDtoList")
    private ArrayList<GoalCategoryDetail> goalCategoryList;
}
