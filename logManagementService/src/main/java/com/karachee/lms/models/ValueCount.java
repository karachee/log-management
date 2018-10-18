package com.karachee.lms.models;

public class ValueCount {

    private String value;
    private Long count;

    public ValueCount(String value, Long count) {
        this.value = value;
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
