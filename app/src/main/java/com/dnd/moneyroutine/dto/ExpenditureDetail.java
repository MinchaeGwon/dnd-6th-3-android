package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpenditureDetail extends ExpenditureCompact implements Serializable {
    private int expenditureId;
    private int categoryId;
    private String name;
    private String detail;
    private int expense;
    private boolean custom;
}
