package edu.uw.tcss450.tcss450group82022.ui.weather;

import java.util.Arrays;
import java.util.List;

public class HourGenerator {

    private static final HourPost[] HOURS;
    public static final int COUNT = 12;

    static {
        HOURS = new HourPost[COUNT];
        for (int i = 0; i < HOURS.length; i++) {
            HOURS[i] = new HourPost
                    .Builder("12 PM", "45", "windy")

                    .build();
        }
    }

    public static List<HourPost> getHourList(){
        return Arrays.asList(HOURS);
    }

    public static HourPost[] getHours(){
        return Arrays.copyOf(HOURS,HOURS.length);
    }

    private HourGenerator(){

    }
}
