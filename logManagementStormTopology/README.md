# Log Management Storm Topology

## Description
Storm Topology to consume log messages


## Kafka
Tested with Kafka 2.11-0.8.2.0

### Start Services
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties

### Topic
logManagement

#### Create Topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic logManagement

#### Delete Topic
bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic logManagement

#### View Topics
bin/kafka-topics.sh --zookeeper localhost:2181 --list
