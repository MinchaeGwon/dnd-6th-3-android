package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCompact implements Serializable {
    private int categoryId;
    private String emoji;
    private String name;
    private String detail;
    private boolean custom;
}
