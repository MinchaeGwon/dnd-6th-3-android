package com.dnd.moneyroutine.custom;

import android.util.Log;

import com.dnd.moneyroutine.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    // 지출 날짜에 사용
    public static String getExpenseCalendarDate(Calendar selectDate) {
        return (selectDate.get(Calendar.MONTH) + 1) + "월 " + (selectDate.get(Calendar.DATE)) + "일 " + getDayOfWeek(selectDate.get(Calendar.DAY_OF_WEEK));
    }

    public static String getCalendarToString(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date.getTime());
    }

    // 메인화면 지난 기록 날짜에 사용
    public static String getLocalDateToString(LocalDate selectDate) {
        String result = "";
        try {
            Calendar regDate = Calendar.getInstance();
            regDate.clear();
            regDate.set(selectDate.getYear(), selectDate.getMonthValue() - 1, selectDate.getDayOfMonth());

            Calendar calendar = Calendar.getInstance(); // 현재 날짜

            long curTime = calendar.getTimeInMillis(); // 현재 시간
            long regTime = regDate.getTimeInMillis(); // 선택된 시간
            long diffTime = (curTime - regTime) / 1000; // 두 시간의 차이

            diffTime /= TIME_MAXIMUM.SEC;
            diffTime /= TIME_MAXIMUM.MIN;
            diffTime /= TIME_MAXIMUM.HOUR;

            if (diffTime >= 0) { // 과거 ~ 오늘
                if (diffTime / TIME_MAXIMUM.DAY < TIME_MAXIMUM.MONTH) {
                    result += (regDate.get(Calendar.MONTH) + 1) + "월";
                } else {
                    result += regDate.get(Calendar.YEAR) + "년 " + (regDate.get(Calendar.MONTH) + 1) + "월 ";
                }
            } else { // 미래
                if(regDate.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
                    result += regDate.get(Calendar.YEAR) + "년 " + (regDate.get(Calendar.MONTH) + 1) + "월 ";
                } else {
                    result += (regDate.get(Calendar.MONTH) + 1) + "월";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
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
