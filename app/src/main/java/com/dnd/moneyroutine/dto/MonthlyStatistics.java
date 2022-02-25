package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyStatistics {
    @SerializedName("topCategory")
    private String topCategory;
    @SerializedName("totalExpense")
    private int totalExpense;
    @SerializedName("goalCategoryInfoDtoList")
    private List<GoalCategoryInfo> goalCategoryInfoDtoList;

}
