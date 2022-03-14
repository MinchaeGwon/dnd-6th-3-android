package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoalCategoryInfo implements Comparable<GoalCategoryInfo>, Serializable {
    private CategoryType categoryType;
    private String categoryName;
    private int percentage;
    private int expense;

    @SerializedName("weeklyExpenditureDetailDtoList")
    private ArrayList<ExpenditureDetailDto> expenditureList;

    @Override
    public int compareTo(GoalCategoryInfo goalCategoryInfo) {
        if (goalCategoryInfo.expense < expense){
            return 1;
        } else if(goalCategoryInfo.expense > expense){
            return -1;
        }

        return 0;
    }
}
