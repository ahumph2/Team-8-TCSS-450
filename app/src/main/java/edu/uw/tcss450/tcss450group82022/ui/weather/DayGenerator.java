package edu.uw.tcss450.uiandnavigationlab.ui.weather;

import java.util.Arrays;
import java.util.List;

public class DayGenerator {

    private static final DayPost[] DAYS;
    public static final int COUNT = 5;

    static {
        DAYS = new DayPost[COUNT];
        for (int i = 0; i < DAYS.length; i++) {
            DAYS[i] = new DayPost
                    .Builder("Wednesday", "45", "25",
                    "Sunny","Windy")

                    .build();
        }
    }

    public static List<DayPost> getDayList(){
        return Arrays.asList(DAYS);
    }

    public static DayPost[] getHours(){
        return Arrays.copyOf(DAYS,DAYS.length);
    }

    private DayGenerator(){

    }
}
