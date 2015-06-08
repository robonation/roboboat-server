package com.felixpageau.roboboat.mission2015.server;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class Config {
  public static final ThreadLocal<DateTimeFormatter> DATE_FORMATTER = new ThreadLocal<DateTimeFormatter>() {
    @Override
    protected DateTimeFormatter initialValue() {
      return DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ssz");
    }
  };
  public static final AtomicInteger TIME_SLOT_DURATION_MIN = new AtomicInteger(30);
  public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("US/Eastern");
}
