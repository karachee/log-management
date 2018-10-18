package com.karachee.lmka;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Plugin(name = "LogManagement", category = "Core", elementType = "appender", printObject = true)
public class LogManagementKafkaAppender extends AbstractAppender {

    private final String DEFAULT_TOPIC = "logManagement";
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";

    private KafkaProducer<String, String> producer = null;
    private String topic;
    private boolean syncSend;

    protected LogManagementKafkaAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, KafkaProducer<String, String> producer, String topic, boolean syncSend) {
        super(name, filter, layout, ignoreExceptions);
        this.producer = producer;
        this.syncSend = syncSend;
        this.topic = (StringUtils.isBlank(topic)) ? DEFAULT_TOPIC : topic;
    }

    @PluginFactory
    public static LogManagementKafkaAppender createAppender(@PluginAttribute("name") final String name,
                                                            @PluginAttribute("topic") String topic,
                                                            @PluginAttribute("ignoreExceptions") final String ignore,
                                                            @PluginAttribute("serviceName") final String serviceName,
                                                            @PluginAttribute("enable") String enable,
                                                            @PluginAttribute("syncSend") String syncSend,
                                                            @PluginElement("Properties") final Property[] properties,
                                                            @PluginElement("Filter") final Filter filter) {

        boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
        boolean enableKafka = Booleans.parseBoolean(enable, true);
        boolean syncSendBool = Booleans.parseBoolean(syncSend, false);

        KafkaProducer<String, String> producer = null;
        Map<String, Object> props = (ArrayUtils.isNotEmpty(properties)) ? Arrays.stream(properties).collect(Collectors.toMap(k -> k.getName(), v -> v.getValue())) : new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        if (!props.containsKey("bootstrap.servers")) {
            props.put("bootstrap.servers", DEFAULT_BOOTSTRAP_SERVERS);
        }

        if (enableKafka) {
            producer = new KafkaProducer<>(props);
        }

        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Layout<? extends Serializable> layout = PatternLayout.newBuilder().withPattern(serviceName + " _|_ " + hostName + " _|_ %date _|_ %C _|_ %L _|_ %level{WARN=Warning, DEBUG=Debug, ERROR=Error, TRACE=Trace, INFO=Info} _|_ %message").build();
        return new LogManagementKafkaAppender(name, filter, layout, ignoreExceptions, producer, topic, syncSendBool);
    }

    @Override
    public final void stop() {
        super.stop();
        if (producer != null) {
            producer.close();
        }
    }

    public void append(LogEvent event) {
        try {
            if (producer != null) {
                Future<RecordMetadata> result = producer.send(new ProducerRecord<>(topic, getLayout().toSerializable(event).toString()));
                if (syncSend) {
                    result.get();
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Error publishing event to Kafka for appender [{}].", this.getName(), e);
            throw new AppenderLoggingException("Unable to publish to kafka for appender: " + e.getMessage(), e);
        } finally {
        }
    }
}
