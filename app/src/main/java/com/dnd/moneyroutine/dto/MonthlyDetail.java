package com.dnd.moneyroutine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MonthlyDetail {
    private String expenseDetail;
    private int expense;

    // 나머지 카테고리를 위해 추가한 것
    private String categoryName;
}
