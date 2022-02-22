package com.dnd.moneyroutine.dto;

import com.dnd.moneyroutine.enums.GoalState;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoalInfo implements Serializable {
    private Long goalId;
    private GoalState goalState;
    private int remainder;
    private int totalBudget;

    @SerializedName("goalCategoryDetailDtoList")
    private ArrayList<GoalCategory> goalCategoryList;
}
