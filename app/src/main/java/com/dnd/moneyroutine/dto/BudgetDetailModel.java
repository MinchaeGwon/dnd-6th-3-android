package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDetailModel {

//    private List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList=null;
    private List<GoalCategoryCreateDto>  goalCategoryCreateDtoList;
    private int total_budget;

}
