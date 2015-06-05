package com.felixpageau.roboboat.mission2015.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class Config {
    public static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy.MM.dd-HH:mm:ssz");
        }
    };
    public static final AtomicInteger TIME_SLOT_DURATION_MIN = new AtomicInteger(30);
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("US/Eastern");
}
