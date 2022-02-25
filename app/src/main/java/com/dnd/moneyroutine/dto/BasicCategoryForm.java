package com.dnd.moneyroutine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicCategoryForm {
    private int goalId;
    private int categoryId;
    private boolean custom;
}
