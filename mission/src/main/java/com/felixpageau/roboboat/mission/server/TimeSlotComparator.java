package com.felixpageau.roboboat.mission.server;

import java.util.Comparator;

public class TimeSlotComparator implements Comparator<TimeSlot> {
    @Override
    public int compare(TimeSlot o1, TimeSlot o2) {
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        
        if (o1.getStartTime().equals(o2.getStartTime())) {
            return o1.getCourse().compareTo(o2.getCourse());
        }
        return o1.getStartTime().isBefore(o2.getStartTime()) ? -1 : 1;
    }
}