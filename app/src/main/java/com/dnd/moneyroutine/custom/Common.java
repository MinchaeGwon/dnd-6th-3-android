package com.dnd.moneyroutine.custom;

import com.dnd.moneyroutine.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class Common {

    private static String getDayOfWeek(int day) {
        String dayOfWeek = "";
        switch (day) {
            case 1:
                dayOfWeek = "일요일";
                break;
            case 2:
                dayOfWeek = "월요일";
                break;
            case 3:
                dayOfWeek = "화요일";
                break;
            case 4:
                dayOfWeek = "수요일";
                break;
            case 5:
                dayOfWeek = "목요일";
                break;
            case 6:
                dayOfWeek = "금요일";
                break;
            case 7:
                dayOfWeek = "토요일";
                break;
        }

        return dayOfWeek;
    }

    // 지출 날짜에 사용
    public static String getExpenseCalendarDate(Calendar selectDate) {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) != selectDate.get(Calendar.YEAR)) {
            return selectDate.get(Calendar.YEAR) + "년 " + (selectDate.get(Calendar.MONTH) + 1) + "월 "
                    + selectDate.get(Calendar.DATE) + "일 " + getDayOfWeek(selectDate.get(Calendar.DAY_OF_WEEK));
        } else {
            return (selectDate.get(Calendar.MONTH) + 1) + "월 " + selectDate.get(Calendar.DATE) + "일 " + getDayOfWeek(selectDate.get(Calendar.DAY_OF_WEEK));
        }
    }

    public static String getCalendarToString(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date.getTime());
    }

    // 메인화면 지난 기록 날짜에 사용
    public static String getMainLocalDateToString(LocalDate selectDate) {
        LocalDate today = LocalDate.now();

        if (selectDate.getYear() == today.getYear()) {
            return selectDate.getMonthValue() + "월";
        } else {
            return selectDate.getYear() + "년 " + selectDate.getMonthValue() + "월";
        }
    }

    // 다이어리 주별 날짜에 사용
    public static String getWeeklyCalendarToString(Calendar calendar) {
        Calendar today = Calendar.getInstance();

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            return (calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.WEEK_OF_MONTH) + "주차";
        } else {
            return calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.WEEK_OF_MONTH) + "주차";
        }
    }

    public static String getExpenditureWeekString(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + ". " + (calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.WEEK_OF_MONTH) + "주차";
    }

    public static int getBasicColorCategoryResId(String category) {
        switch (category) {
            case "카페":
                return R.drawable.coffee_color;
            case "식비":
                return R.drawable.food_color;
            case "유흥비":
                return R.drawable.beer_color;
            case "자기계발":
                return R.drawable.book_color;
            case "교통비":
                return R.drawable.bus_color;
            case "쇼핑":
                return R.drawable.bag_color;
            case "정기구독":
                return R.drawable.computer_color;
            case "생활용품":
                return R.drawable.tissue_color;
            case "건강":
                return R.drawable.pill_color;
        }

        return -1;
    }

    public static int getBasicGrayCategoryResId(String category) {
        switch (category) {
            case "카페":
                return R.drawable.coffee_gray;
            case "식비":
                return R.drawable.food_gray;
            case "유흥비":
                return R.drawable.beer_gray;
            case "자기계발":
                return R.drawable.book_gray;
            case "교통비":
                return R.drawable.bus_gray;
            case "쇼핑":
                return R.drawable.bag_gray;
            case "정기구독":
                return R.drawable.computer_gray;
            case "생활용품":
                return R.drawable.tissue_gray;
            case "건강":
                return R.drawable.pill_gray;
        }

        return -1;
    }
}
