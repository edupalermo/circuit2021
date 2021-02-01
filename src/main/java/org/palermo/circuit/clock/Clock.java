package org.palermo.circuit.clock;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Clock {

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
        if (nano >= 1000) {
            long micro = nano / 1000L;
            return formatMicro(micro) +  " " + threeDigits.get().format(nano % 1000L) + " ns";
        }
        else {
            return threeDigits.get().format(nano) + " ns";
        }

    }

    private String formatMicro(long micro) {
        if (micro >= 1000) {
            long milliSecond = micro / 1000L;
            return formatMilliSecond(milliSecond) +  " " + threeDigits.get().format(micro % 1000L) + " us";
        }
        else {
            return threeDigits.get().format(micro) + " us";
        }

    }

    private String formatMilliSecond(long milliSecond) {
        if (milliSecond >= 1000) {
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
        return  minute + " m";
    }

}
