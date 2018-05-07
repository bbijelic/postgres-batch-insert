package com.github.bbijelic.pbi;

import com.github.bbijelic.pbi.entity.TestTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityTransaction;
import java.util.List;

@DisplayName("Batch Enabled Flush Insert Benchmark")
public class BatchEnabledFlushInsertMultilineBenchmark extends BaseBenchmark {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchEnabledFlushInsertMultilineBenchmark.class);

    /**
     * Persistence unit name
     */
    @Override
    protected String getPersistenceUnitName(){
        return "BatchEnabledMultilinePersistenceUnit";
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 10000, 100000, 500000, 1000000})
    public void benchmark(final int recordsAmount){
        LOGGER.info("Executing test for {} records", recordsAmount);

        // Generate data
        final List<TestTable> records = generateData(recordsAmount);

        // Start stopwatch
        startStopwatch();

        // Get and begin transaction
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // Iterate and persist data
        for (int i = 0; i < records.size(); i++) {
            entityManager.persist(records.get(i));

            if (i % 100 == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }

        }

        // Commit transaction
        transaction.commit();

        // Log time
        logTime(recordsAmount);
    }

}
