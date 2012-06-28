package com.github.lambdas;

import ch.lambdaj.Lambda;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static java.text.MessageFormat.format;

public abstract class AbstractMeasurementTest {
    private static final transient Logger logger = LoggerFactory.getLogger(PrintAllBrandsTest.class);
    public static final int ITERATIONS_COUNT = 100000;
    public static final int MEASUREMENTS_COUNT = 20;

    protected void performMeasurements(final Supplier<Void> functionToMeasure) {
        logger.info(format("================<{0}>================", functionToMeasure.getClass().getSimpleName()));

        final ArrayList<Long> measurements = Lists.newArrayList();

        for (int i = 0; i < MEASUREMENTS_COUNT; i++) {
            measurements.add(performMeasurement(functionToMeasure));
        }

        logger.info(format("Min elapsed time: {0}", Lambda.min(measurements)));
        logger.info(format("Max elapsed time: {0}", Lambda.max(measurements)));
        logger.info(format("Avg elapsed time: {0}", Lambda.avg(measurements)));
    }

    private long performMeasurement(final Supplier<Void> toMeasure) {

        final Stopwatch stopWatch = new Stopwatch();
        stopWatch.start();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            toMeasure.get();
        }
        stopWatch.stop();

        return stopWatch.elapsedMillis();
    }
}
