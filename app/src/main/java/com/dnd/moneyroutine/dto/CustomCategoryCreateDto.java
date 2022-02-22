package com.dnd.moneyroutine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomCategoryCreateDto {

    private String detail;
    private String emoji;
    private String name;

}
