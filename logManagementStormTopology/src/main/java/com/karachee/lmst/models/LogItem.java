package com.karachee.lmst.models;

import java.util.Date;

public class LogItem {

    private String service;
    private String hostname;
    private Date dateTime;
    private String packageAndClass;
    private Integer lineNumber;
    private String level;
    private String message;

    public LogItem(String service, String hostname, Date dateTime, String packageAndClass, Integer lineNumber, String level, String message) {
        this.service = service;
        this.hostname = hostname;
        this.packageAndClass = packageAndClass;
        this.message = message;
        this.dateTime = dateTime;
        this.lineNumber = lineNumber;
        this.level = level;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getPackageAndClass() {
        return packageAndClass;
    }

    public void setPackageAndClass(String packageAndClass) {
        this.packageAndClass = packageAndClass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
