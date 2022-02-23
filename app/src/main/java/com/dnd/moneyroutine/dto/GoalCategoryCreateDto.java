package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalCategoryCreateDto implements Serializable {

    private int budget;
    private Long categoryId;
    private Boolean isCustom;

}
