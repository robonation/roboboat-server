package com.felixpageau.roboboat.mission2014.server;

import java.util.Comparator;
import java.util.Map;

public class TimeSlotComparatorEntry<V> implements Comparator<Map.Entry<TimeSlot, V>> {
    private final boolean desc;
    public TimeSlotComparatorEntry(boolean desc) {
        this.desc = desc;
    }
    
    @Override
    public int compare(Map.Entry<TimeSlot, V> o1, Map.Entry<TimeSlot, V> o2) {
        if (o1 == null || o1.getKey() == null) return -1;
        if (o2 == null || o2.getKey() == null) return 1;
        
        if (o1.getKey().getStartTime().equals(o2.getKey().getStartTime())) {
            return o1.getKey().getCourse().compareTo(o2.getKey().getCourse());
        }
        if (desc) {
            return o1.getKey().getStartTime().isBefore(o2.getKey().getStartTime()) ? 1 : -1;
        }
        return o1.getKey().getStartTime().isBefore(o2.getKey().getStartTime()) ? -1 : 1;
    }
}