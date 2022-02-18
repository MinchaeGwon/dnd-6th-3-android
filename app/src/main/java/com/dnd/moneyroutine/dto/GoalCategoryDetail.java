package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GoalCategoryDetail {
    private int categoryId;
    private String name;
    private int budget;

    @SerializedName("total_expensive")
    private int totalExpensive;

    private boolean isCustom;
    private String emoji;
}
