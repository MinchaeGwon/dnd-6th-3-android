package com.dnd.moneyroutine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class GoalCategoryCreateDtoList {

    private Boolean isCustom;
    private String categoryId;
    private String budget;

    public GoalCategoryCreateDtoList(Boolean isCustom, String categoryId, String budget) {
        this.isCustom = isCustom;
        this.categoryId = categoryId;
        this.budget = budget;
    }

    public Boolean getCustom() {
        return isCustom;
    }

    public void setCustom(Boolean custom) {
        isCustom = custom;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "GoalCategoryCreateDtoList{" +
                "isCustom=" + isCustom +
                ", categoryId='" + categoryId + '\'' +
                ", budget='" + budget + '\'' +
                '}';
    }
}
