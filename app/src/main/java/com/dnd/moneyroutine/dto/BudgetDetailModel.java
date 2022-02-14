package com.dnd.moneyroutine.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDetailModel {

    private List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList = new ArrayList<>();
    private String total_budget;

    public BudgetDetailModel(List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList, String total_budget) {
        this.goalCategoryCreateDtoList = goalCategoryCreateDtoList;
        this.total_budget = total_budget;
    }

    public List<GoalCategoryCreateDtoList> getGoalCategoryCreateDtoList() {
        return goalCategoryCreateDtoList;
    }

    public void setGoalCategoryCreateDtoList(List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList) {
        this.goalCategoryCreateDtoList = goalCategoryCreateDtoList;
    }

    public String getTotal_budget() {
        return total_budget;
    }

    public void setTotal_budget(String total_budget) {
        this.total_budget = total_budget;
    }

    @Override
    public String toString() {
        return "BudgetDetailModel{" +
                "goalCategoryCreateDtoList=" + goalCategoryCreateDtoList +
                ", total_budget='" + total_budget + '\'' +
                '}';
    }
}
