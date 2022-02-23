package com.dnd.moneyroutine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DirectCustomCategoryForm {
    private int goalId;
    private String emoji;
    private String name;
    private String detail;
    private int budget;
}
