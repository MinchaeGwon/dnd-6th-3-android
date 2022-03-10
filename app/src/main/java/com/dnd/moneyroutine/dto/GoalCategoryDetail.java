package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
public class GoalCategoryDetail extends CategoryCompact implements Serializable {
    private int goalCategoryId;
    private int budget;
    private int totalExpense;
}