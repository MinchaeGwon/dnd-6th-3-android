package com.dnd.moneyroutine.custom;

import java.text.SimpleDateFormat;
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

    public static String getExpenseCalendarDate(Calendar selectDate) {
        return (selectDate.get(Calendar.MONTH) + 1) + "월 " + (selectDate.get(Calendar.DATE)) + "일 " + getDayOfWeek(selectDate.get(Calendar.DAY_OF_WEEK));
    }

    public static String getCalendarToString(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date.getTime());
    }
}
