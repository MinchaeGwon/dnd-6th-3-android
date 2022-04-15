package com.dnd.moneyroutine.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpenditureCompact implements Serializable {
    private LocalDate date;
    private String expenseDetail;
    private String emotionDetail;

    // 나머지 카테고리를 위해 추가한 것
    private String categoryName;
}
