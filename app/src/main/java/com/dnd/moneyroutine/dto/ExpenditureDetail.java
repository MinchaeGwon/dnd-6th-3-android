package com.dnd.moneyroutine.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureDetail implements Serializable {
    private LocalDate date;
    private int expense;
    private String expenseDetail;

    // 나머지 카테고리를 위해 추가한 것 : 주별 상세 소비 내역 카테고리에 사용
    private String categoryName;
}
