package com.karachee.lmst;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karachee.lmst.models.LogItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Log4jBolt extends BaseBasicBolt {
    static Logger logger = LogManager.getLogger(Log4jBolt.class);

    private DateTimeFormatter formatter = null;

    private final String logManagementServicUrl = "http://localhost:8080/logManagementService/pdls/v1/";
    private static ObjectMapper objectMapper;

    private Map<String, String> postHeaders = new HashMap<String, String>() {{
        put("Content-type", "application/json");
    }};

    private final String LOG_DELIMITER = " _\\|_ ";

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
        objectMapper = new ObjectMapper();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        System.out.println("Entering Log4jBolt");

        String logMessageString = new String(tuple.getBinary(0));
        String[] logArr = (logMessageString != null) ? logMessageString.split(LOG_DELIMITER) : null;
        if (ArrayUtils.isNotEmpty(logArr)) {
            if (logArr.length == 7) {
                persistsToDb(buildLogItem(logArr[0], logArr[1], logArr[2], logArr[3], logArr[4], logArr[5], logArr[6]));
            } else {
                logger.error("Incorrect log4j2 Kafka appender on the logging service. Incoming log message: {}", logMessageString);
            }
        }
    }

    private LogItem buildLogItem(String service, String hostname, String dateString, String packageAndClass, String lineNumberString, String level, String message) {
        LogItem logItem = null;

        Date date = getDateFromString(dateString);
        Integer lineNumber = getIntegerFromString(lineNumberString);

        if (StringUtils.isNotBlank(service) && StringUtils.isNotBlank(hostname) && date != null && StringUtils.isNotBlank(packageAndClass) && lineNumber != null && StringUtils.isNotBlank(level) && StringUtils.isNotBlank(message)) {
            logItem = new LogItem(service, hostname, date, packageAndClass, lineNumber, level, message);
        }

        return logItem;
    }

    private Integer getIntegerFromString(String lineNumberString) {
        Integer integer = null;

        try {
            integer = (StringUtils.isNotEmpty(lineNumberString)) ? Integer.parseInt(lineNumberString) : null;
        } catch (Exception e) {

        }
        return integer;
    }

    private Date getDateFromString(String dateString) {
        Date date = null;

        try {
            LocalDateTime dateTime = (StringUtils.isNotBlank(dateString)) ? LocalDateTime.parse(dateString, formatter) : null;
            date = (dateTime != null) ? Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
        } catch (Exception e) {

        }

        return date;
    }

    private void persistsToDb(LogItem logItem) {
        if (logItem != null) {
            try {
                //httpClient.post(logManagementServicUrl + "log", objectMapper.writeValueAsString(logItem), null, postHeaders);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
