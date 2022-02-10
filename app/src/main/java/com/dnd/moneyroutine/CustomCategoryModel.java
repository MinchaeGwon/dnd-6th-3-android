package com.dnd.moneyroutine;

public class CustomCategoryModel {
    private String detail;
    private String name;
    private int userid;


    public CustomCategoryModel(String detail, String name, int userid){
        this.detail=detail;
        this.name=name;
        this.userid=userid;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

}
