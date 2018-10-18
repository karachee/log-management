package com.karachee.lms.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "log.log_item")
@Table(schema = "`log`", name = "`log_item`")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter("LogItem")
public class LogItem {

    @Id
    @SequenceGenerator(
            name = "id_increment",
            sequenceName = "log.id_increment",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "id_increment"
    )
    @Column(
            name = "`id`",
            nullable = false
    )
    private Integer id;

    @Column(
            name = "`service`",
            nullable = false
    )
    private String service;

    @Column(
            name = "`hostname`",
            nullable = false
    )
    private String hostname;

    @Column(
            name = "`date_time`",
            nullable = false
    )
    private Date dateTime;

    @Column(
            name = "`package_and_class`",
            nullable = false
    )
    private String packageAndClass;

    @Column(
            name = "`message`",
            nullable = false
    )
    private String message;

    @Column(
            name = "`line_number`",
            nullable = false
    )
    private Integer lineNumber;

    @Column(
            name = "`level`",
            nullable = false
    )
    private String level;

    public LogItem(){

    }

    public LogItem(String service, String hostname, Date dateTime, String packageAndClass, Integer lineNumber, String level, String message) {
        this.service = service;
        this.hostname = hostname;
        this.packageAndClass = packageAndClass;
        this.message = message;
        this.dateTime = dateTime;
        this.level = level;
        this.lineNumber = lineNumber;
    }

    public LogItem(int id, String service, String hostname, Date dateTime, String packageAndClass, Integer lineNumber, String level, String message) {
        this.id = id;
        this.hostname = hostname;
        this.service = service;
        this.packageAndClass = packageAndClass;
        this.message = message;
        this.dateTime = dateTime;
        this.level = level;
        this.lineNumber = lineNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
