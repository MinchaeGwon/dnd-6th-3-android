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
public class ExpenditureDetailDto implements Serializable {
    private LocalDate date;
    private int expense;
    private String expenseDetail;
    private String categoryName;
    private CategoryType categoryType;
}
