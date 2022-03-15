package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryType implements Serializable {
    private int categoryId;
    private boolean custom;
}
