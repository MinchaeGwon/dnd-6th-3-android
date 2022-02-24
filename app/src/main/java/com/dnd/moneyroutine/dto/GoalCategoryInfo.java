package com.dnd.moneyroutine.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalCategoryInfo implements Comparable<GoalCategoryInfo> {

    private CategoryType categoryType;
    private String categoryName;
    private int percentage;
    private int expense;
    private List<ExpenditureDetailDto> weeklyExpenditureDetailDtoList;

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
