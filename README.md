
# Environment

Postgres DB running in a Docker container ona Ubuntu virtual machine.
Tests executed via InteliJ on Windows 10 Pro host machine.

Hibernate logging is turned OFF.
Postgres statement logging is turned ON.

JDK 9 is used.

# Benchmark

Benchmark is implemented as JUnit 5 tests:

    com.github.bbijelic.pbi.NoBatchInsertBenchmark
    com.github.bbijelic.pbi.BatchEnabledInsertBenchmark
    com.github.bbijelic.pbi.BatchEnabledFlushInsertBenchmark
    com.github.bbijelic.pbi.BatchEnabledFlushInsertMultilineBenchmark

# Benchmark Results

![](http://i64.tinypic.com/315l8g0.jpg)

## No Batch

For each INSERT, Hibernate does 1 round-trip to database.

| Records | 10  | 100  | 1000  | 10000   | 100000  |  500000 | 1000000  |
| :------------| :------------ | :------------ | :------------ | :------------ | :------------ | :------------ | :------------ |
| Duration (ms) |  151  | 358   |  2060  | 11394   | 101164  | 591409 | 1135430  |

## Batch Enabled
Batch size configured to 100.

Hibernate does 1 round-trip to database for each 100 persists.
100 INSERT statements are batched together.

Batch handling is enabled by adding following parameters to the JPA persistence unit:

    <property name="hibernate.jdbc.batch_size" value="100"/>
    <property name="hibernate.order_inserts" value="true"/>
    <property name="hibernate.order_updates" value="true"/>

More on this link: [How To Batch Insert And Update Statements With Hibernate](http://https://vladmihalcea.com/how-to-batch-insert-and-update-statements-with-hibernate/ "How To Batch Insert And Update Statements With Hibernate")

### Batch Enabled, No Entity Manager Flush

Hibernate does 1 round-trip to database for each 100 persists.
100 INSERT statements are batched together.

| Records | 10  | 100  | 1000  | 10000   | 100000 | 500000 | 1000000  |
| :------------| :------------ | :------------ | :------------ | :------------ | :------------ | :------------ | :------------ |
| Duration (ms) |  95  | 222   |  603  | 2703   | 22011 | 106716 | 217013  |

### Batch Enabled, With Entity Manager Flush

Hibernate does 1 round-trip to database for each 100 persists.
100 INSERT statements are batched together.
Persistence context is flushed and cleaned on every 100 persists.

| Records | 10  | 100  | 1000  | 10000   | 100000 | 500000 | 1000000  |
| :------------| :------------ | :------------ | :------------ | :------------ | :------------ | :------------ | :------------ |
| Duration (ms) |  175  | 272   |  574  | 3020   | 19237 | 102778  | 202819  |

### Batch Enabled, With Entity Manager Flush, Multiline Insert

Hibernate does 1 round-trip to database for each 100 persists.
100 INSERT statements are batched together.
Persistence context is flushed and cleaned on every 100 persists.
Multiple INSERT statements are combined into single one.

Multiline feature enabled by setting JDBC parameter via connection URL:

    jdbc:postgresql://localhost:5432/test?reWriteBatchedInserts=true

More on this link: [ReWriteBatchedInserts Tutorial](http://https://vladmihalcea.com/postgresql-multi-row-insert-rewritebatchedinserts-property/ "ReWriteBatchedInserts Tutorial")

| Records | 10  | 100  | 1000  | 10000   | 100000 | 500000 | 1000000  |
| :------------| :------------ | :------------ | :------------ | :------------ | :------------ | :------------ | :------------ |
| Duration (ms) |  100  | 73   |  327  | 1177   | 6906 | 37146 | 71301  |

# Batch Size Benchmark

Obvious winner of the benchmark ofcourse is the multiline setup with batch feature. Now, we need determine how Postgres behaves for different values of batch size against records.

Postgres JDBC driver rewrites INSERT statements into multiline ones, but number of records stored per multiline statement can only be power of 2, so power of 2 values for the batch size are chosen.

For example, batch size of 10 would be split into two multiline INSERT's since first lower power of 2 is 8. First insert would have 8 records, and second only 2 record, which totals batch of 10.


![](http://i66.tinypic.com/2hz7as6.png)

## Conclusion

For larger numbers of records, larger batch sizes are better, but from benchmark it can be observed that batch size of 1024 is the sweetspot. One would benefit very low or none from batch size of 2024.