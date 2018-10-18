package com.karachee.lmst;

import kafka.api.OffsetRequest;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.topology.TopologyBuilder;

public class StormLog4jTopology {

    public static void main(String[] args) {
        final String spoutId = "log-management-spout";

        TopologyBuilder topologyBuilder = new TopologyBuilder();

        BrokerHosts hosts = new ZkHosts("localhost:2181", "/brokers");
        SpoutConfig spoutConfig = new SpoutConfig(hosts, "logManagement", "/consumers", spoutId);
        spoutConfig.startOffsetTime = OffsetRequest.LatestTime();
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

        topologyBuilder.setSpout(spoutId, kafkaSpout);
        topologyBuilder.setBolt("log-management-bolt", new Log4jBolt()).shuffleGrouping(spoutId);

        Config config = new Config();
        config.setDebug(false);

        org.apache.storm.generated.StormTopology stormTopology = topologyBuilder.createTopology();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("log-management-topology", config, stormTopology);
    }
}
