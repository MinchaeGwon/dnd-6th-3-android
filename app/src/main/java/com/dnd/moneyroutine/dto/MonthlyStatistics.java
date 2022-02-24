package com.dnd.moneyroutine.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyStatistics {

    private String topCategory;
    private Long totalExpense;
    private List<GoalCategoryInfo> goalCategoryInfoDtoList;

}
