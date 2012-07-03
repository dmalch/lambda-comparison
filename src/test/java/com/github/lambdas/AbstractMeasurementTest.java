package com.github.lambdas;

import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import static java.text.MessageFormat.format;

public abstract class AbstractMeasurementTest {
    private static final transient Logger logger = LoggerFactory.getLogger(AbstractMeasurementTest.class);
    public static final int ITERATIONS_COUNT = 100000;
    public static final int MEASUREMENTS_COUNT = 100;
    public static final int WARMUP_MEASUREMENTS_COUNT = 100;

    protected void performMeasurements(final Supplier functionToMeasure) {
        logger.info(format("================<{0}>================", functionToMeasure.getClass().getSimpleName()));

        warmup(functionToMeasure);
        final ArrayList<Long> statistics = benchmark(functionToMeasure);
        printStatistics(functionToMeasure, statistics);
    }

    private void printStatistics(final Supplier functionToMeasure, final ArrayList<Long> statistics) {
        final StatisticalSummary descriptiveStatistics = new DescriptiveStatistics(Doubles.toArray(statistics));
        logger.info(format("{0}: Min elapsed time: {1}", functionToMeasure.getClass().getSimpleName(), descriptiveStatistics.getMin()));
        logger.info(format("{0}: Max elapsed time: {1}", functionToMeasure.getClass().getSimpleName(), descriptiveStatistics.getMax()));
        logger.info(format("{0}: Avg elapsed time: {1}", functionToMeasure.getClass().getSimpleName(), descriptiveStatistics.getMean()));
        logger.info(format("{0}: Standard deviation: {1}", functionToMeasure.getClass().getSimpleName(), descriptiveStatistics.getStandardDeviation()));
        logger.info(format("{0}: Confidence interval width: {1}", functionToMeasure.getClass().getSimpleName(), getConfidenceIntervalWidth(descriptiveStatistics, 0.95)));
    }

    private double getConfidenceIntervalWidth(final StatisticalSummary statisticalSummary, final double significance) {
        final TDistribution tDist = new TDistribution(statisticalSummary.getN() - 1);
        final double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
        return a * statisticalSummary.getStandardDeviation() / Math.sqrt(statisticalSummary.getN());
    }

    private void warmup(final Supplier functionToMeasure) {
        for (int i = 0; i < WARMUP_MEASUREMENTS_COUNT; i++) {
            performMeasurement(functionToMeasure);
        }
    }

    private ArrayList<Long> benchmark(final Supplier functionToMeasure) {
        final ArrayList<Long> measurements = Lists.newArrayList();

        for (int i = 0; i < MEASUREMENTS_COUNT; i++) {
            measurements.add(performMeasurement(functionToMeasure));
        }
        return measurements;
    }

    private long performMeasurement(final Supplier toMeasure) {

        final Stopwatch stopWatch = new Stopwatch();
        stopWatch.start();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            toMeasure.get();
        }
        stopWatch.stop();

        return stopWatch.elapsedMillis();
    }

    protected <T extends Object & Comparable<? super T>> T calcMax(final Iterable<T> map) {
        return Collections.max(new ArrayList<T>() {
            @Override
            public Iterator<T> iterator() {
                return map.iterator();
            }
        });
    }

    protected <T> T calcMax(final Iterable<T> map, final Comparator<T> comparator) {
        return Collections.max(new ArrayList<T>() {
            @Override
            public Iterator<T> iterator() {
                return map.iterator();
            }
        }, comparator);
    }

    protected <T extends Object & Comparable<? super T>> T calcMin(final Iterable<T> map) {
        return Collections.min(new ArrayList<T>() {
            @Override
            public Iterator<T> iterator() {
                return map.iterator();
            }
        });
    }

    protected <T> T calcMin(final Iterable<T> map, final Comparator<T> comparator) {
        return Collections.min(new ArrayList<T>() {
            @Override
            public Iterator<T> iterator() {
                return map.iterator();
            }
        }, comparator);
    }
}
