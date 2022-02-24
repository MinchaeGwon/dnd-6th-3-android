package com.dnd.moneyroutine.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenditureDetailDto implements Serializable {
    private LocalDate date;
    private Long expense;
    private String expenseDetail;

}
