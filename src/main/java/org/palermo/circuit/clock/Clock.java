package org.palermo.circuit.clock;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Clock {

    private static long MICROSECOND = 1000L;
    private static long MILLISECOND = 1000L * MICROSECOND;
    private static long SECOND = 1000L * MICROSECOND;

    private static ThreadLocal<NumberFormat> threeDigits = new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat initialValue() {
            return new DecimalFormat("000");
        }
    };

    private static ThreadLocal<NumberFormat> twoDigits = new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat initialValue() {
            return new DecimalFormat("00");
        }
    };

    private final long initial;

    private Clock(long initial) {
        this.initial = initial;
    }

    public static Clock start() {
        return new Clock(System.nanoTime());
    }

    public String getDelta() {
        return this.formatNano(System.nanoTime() - this.initial);
    }

    private String formatNano(long nano) {
        if (nano >= 1000 * 1000) { // milliseconds
            return formatMicro(nano / 1000L);
        }
        else if (nano >= 1000) {
            long micro = nano / 1000L;
            return formatMicro(micro) +  " " + threeDigits.get().format(nano % 1000L) + " ns";
        }
        else {
            return threeDigits.get().format(nano) + " ns";
        }

    }

    private String formatMicro(long micro) {
        if (micro >= 1000 * 1000) {  // seconds
            return formatMilliSecond(micro / 1000L);
        }
        else if (micro >= 1000) {
            long milliSecond = micro / 1000L;
            return formatMilliSecond(milliSecond) +  " " + threeDigits.get().format(micro % 1000L) + " us";
        }
        else {
            return threeDigits.get().format(micro) + " us";
        }

    }

    private String formatMilliSecond(long milliSecond) {
        if (milliSecond >= 1000 * 60) { // minutes
            return formatSecond(milliSecond / 1000L);
        }
        else if (milliSecond >= 1000) {
            long second = milliSecond / 1000L;
            return formatSecond(second) +  " " + threeDigits.get().format(milliSecond % 1000L) + " ms";
        }
        else {
            return threeDigits.get().format(milliSecond) + " ms";
        }

    }

    private String formatSecond(long second) {
        if (second >= 60) {
            long minute = second / 60L;
            return formatMinute(minute) +  " " + twoDigits.get().format(second % 60L) + " s";
        }
        else {
            return twoDigits.get().format(second) + " s";
        }
    }

    private String formatMinute(long minute) {
        if (minute >= 60) {
            long hour = minute / 60L;
            return formatHour(hour) +  " " + twoDigits.get().format(minute % 60L) + " m";
        }
        else {
            return  twoDigits.get().format(minute) + " m";
        }
    }

    private String formatHour(long hour) {
        if (hour >= 24) {
            long day = hour / 24L;
            return formatDay(day) +  " " + twoDigits.get().format(hour % 24L) + " h";
        }
        else {
            return twoDigits.get().format(hour) + " h";
        }
    }

    private String formatDay(long day) {
        return  day + " d";
    }

}
