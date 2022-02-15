package com.dnd.moneyroutine.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class BudgetDetailModel {

    private List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList = new ArrayList<>();
    private int total_budget;

    public BudgetDetailModel(List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList, int total_budget) {
        this.goalCategoryCreateDtoList = goalCategoryCreateDtoList;
        this.total_budget = total_budget;
    }

    public List<GoalCategoryCreateDtoList> getGoalCategoryCreateDtoList() {
        return goalCategoryCreateDtoList;
    }

    public void setGoalCategoryCreateDtoList(List<GoalCategoryCreateDtoList> goalCategoryCreateDtoList) {
        this.goalCategoryCreateDtoList = goalCategoryCreateDtoList;
    }

    public int getTotal_budget() {
        return total_budget;
    }

    public void setTotal_budget(int total_budget) {
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
