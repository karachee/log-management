# Log Management Kafka Appender

## Description
Log Management Kafka Appender


## Log4j2

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="info" strict="true" name="XMLConfigTest" packages="com.karachee.lmst">

    <Appenders>
        <LogManagement name="LogManagementAppender" serviceName="TESTING_SERVICE"></LogManagement>
    </Appenders>

    <Loggers>
        <Root level="debug">
            <AppenderRef ref="LogManagementAppender"/>
        </Root>

        <!-- Set component initial logging levels -->
        <Logger name="com.karachee" level="debug"/>
        <Logger name="org.apache" level="warn"/>
        <!-- avoid recursive logging -->
        <Logger name="org.apache.kafka" level="INFO"/>
    </Loggers>
</Configuration>

```
