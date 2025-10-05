package com.baeldung.lju.conditionaltest;


import java.time.DayOfWeek;
import java.time.LocalDate;

public class IsWeekendUtility {
    static boolean isWeekend() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    }
}
