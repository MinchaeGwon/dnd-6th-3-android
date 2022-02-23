package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoalCategoryCompact implements Serializable {
    private int categoryId;
    private String emoji;
    private String name;
    private String detail;
    private boolean custom;
}
