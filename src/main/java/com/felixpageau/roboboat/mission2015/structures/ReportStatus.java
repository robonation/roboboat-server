package com.felixpageau.roboboat.mission2015.structures;

public class ReportStatus {
    private final boolean success;
    
    public ReportStatus(boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }
}
