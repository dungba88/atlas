# atlas

Atlas is a dynamic DAG (directed acyclic graph) task executor. You submit a list of tasks with dependencies, Atlas will sort and execute them based on their orders. In its simplest setup, Atlas runs in a single machine with everything is stored in-mem. But it can be easily extended with pluggable storage/execution engines, e.g:

- Distributed task runners with Hazelcast, Kafka
- Distributed task coordinator with Kafka
- Persistent storage with RocksDB

## install

## getting started
