package com.dnd.moneyroutine.enums;

// 하단 탭에 사용되는 enum
public enum FooterEnum {
    HOME(0, "홈"),
    EXPENDITURE(2, "소비내역"),
    DIARY(1, "다이어리"),
    CHALLENGE(3, "목표달성");

    private int orderingNumber;
    private String name;

    FooterEnum(int orderingNumber, String name) {
        this.orderingNumber = orderingNumber;
        this.name = name;
    }

    public int getOrderingNumber() {
        return this.orderingNumber;
    }

    public String getName() {
        return this.name;
    }

    public static FooterEnum findByOrderingNumber(int orderingNumber) {
        for (FooterEnum footer : FooterEnum.values()) {
            if (footer.getOrderingNumber() == orderingNumber) {
                return footer;
            }
        }
        throw new RuntimeException();
    }
}
