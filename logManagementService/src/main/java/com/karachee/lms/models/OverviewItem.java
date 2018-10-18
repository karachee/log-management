package com.karachee.lms.models;

public class OverviewItem {

    private String service;
    private String level;
    private Long count;

    public OverviewItem(String service, String level, Long count) {
        this.service = service;
        this.level = level;
        this.count = count;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
