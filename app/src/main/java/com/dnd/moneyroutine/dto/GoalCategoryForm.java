package com.dnd.moneyroutine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalCategoryForm {
    private int userId;
    private int goalCategoryId;
    private int changeBudget;
}
