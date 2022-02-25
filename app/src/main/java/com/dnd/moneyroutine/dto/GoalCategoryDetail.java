package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
public class GoalCategoryDetail implements Serializable {
    private int goalCategoryId;
    private int categoryId;
    private String emoji;
    private String name;
    private String detail;
    private int budget;
    private int totalExpense;
    private boolean custom;
}
