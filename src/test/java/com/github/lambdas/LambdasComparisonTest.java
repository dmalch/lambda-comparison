package com.github.lambdas;

import ch.lambdaj.Lambda;
import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static java.text.MessageFormat.format;

public class LambdasComparisonTest {
    private static final transient Logger logger = LoggerFactory.getLogger(LambdasComparisonTest.class);

    public static final int ITERATIONS_COUNT = 100000;
    public static final int MEASUREMENTS_COUNT = 100;

    @Test
    public void testPrintAllBrandsIterable() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsIterable functionToMeasure = new PrintAllBrandsIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testPrintAllBrandsLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsLambdaJ functionToMeasure = new PrintAllBrandsLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    private void performMeasurements(final Supplier<Void> functionToMeasure) {
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

    private class PrintAllBrandsIterable implements Supplier<Void> {
        private final Db db;

        public PrintAllBrandsIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final StringBuilder sb = new StringBuilder();
            for (final Car car : db.getCars()) {
                sb.append(car.getBrand()).append(", ");
            }
            final String brands = sb.toString().substring(0, sb.length() - 2);
            return null;
        }
    }

    private class PrintAllBrandsLambdaJ implements Supplier<Void> {
        private final Db db;

        public PrintAllBrandsLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final String brands = Lambda.joinFrom(db.getCars()).getBrand();
            return null;
        }
    }
}
