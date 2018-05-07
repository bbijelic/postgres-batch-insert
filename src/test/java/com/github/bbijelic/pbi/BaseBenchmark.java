package com.github.bbijelic.pbi;

import com.github.bbijelic.pbi.entity.TestTable;
import com.google.common.base.Stopwatch;
import org.hibernate.type.StringNVarcharType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BaseBenchmark {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseBenchmark.class);

    /**
     * Result map
     */
    private static final Map<Integer, Long> resultMap = new HashMap<>();

    /**
     * Entity manager factory
     */
    protected EntityManagerFactory entityManagerFactory;

    /**
     * Entity manager
     */
    protected EntityManager entityManager;

    /**
     * Persistence unit name
     */
    protected String getPersistenceUnitName(){
        return "";
    }

    @BeforeEach
    public void before(){
        LOGGER.info("Creating entity manager and entity manager factory");

        // Create entity manager factory
        entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
        // Create entity manager
        entityManager = entityManagerFactory.createEntityManager();

    }

    @AfterEach
    public void after(){
        LOGGER.info("Closing entity manager and entity manager factory");

        // Close entity manager
        entityManager.close();
        // Close entity manager factory
        entityManagerFactory.close();
    }

    /**
     * Generates defined amount of records
     *
     * @param amount the amount of records to generate
     * @return list of generated amount data
     */
    protected static List<TestTable> generateData(final int amount){
        final List<TestTable> generatedDataList = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            generatedDataList.add(new TestTable(UUID.randomUUID().toString()));
        }
        return generatedDataList;
    }

    @AfterAll
    public static void outputResults(){
        LOGGER.info("Results: {}", resultMap.toString());
    }

    private Stopwatch stopwatch;

    protected void startStopwatch(){
        stopwatch = Stopwatch.createStarted();
    }

    protected void logTime(final int recordsAmount){
        // Get the duration of the inserts
        final long duration = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        resultMap.put(recordsAmount, duration);

        LOGGER.info("Executed in {} ms", duration);
    }

}
