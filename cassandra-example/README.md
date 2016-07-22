#cassandra cqlsh命令：
* 显示所有keyspace:describe keyspaces
* 创建keyspace:如：create KEYSPACE if not EXISTS test_space WITH replication = {'class': 'SimpleStrategy','replication_factor': 1};
* 使用keyspace:use test_space
