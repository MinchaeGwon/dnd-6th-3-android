package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalCategoryInfo implements Comparable<GoalCategoryInfo> {

    @SerializedName("categoryType")
    private CategoryType categoryType;
    @SerializedName("categoryName")
    private String categoryName;
    @SerializedName("percentage")
    private int percentage;
    @SerializedName("expense")
    private int expense;
    @SerializedName("weeklyExpenditureDetailDtoList")
    private List<ExpenditureDetailDto> expenditureDetailDtoList;

    @Override
    public int compareTo(GoalCategoryInfo goalCategoryInfo) {
        if(goalCategoryInfo.expense<expense){
            return 1;
        }
        else if(goalCategoryInfo.expense>expense){
            return -1;
        }
        return 0;
    }
}
