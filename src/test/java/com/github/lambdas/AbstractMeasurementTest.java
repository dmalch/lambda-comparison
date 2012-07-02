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
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;

public abstract class AbstractMeasurementTest {
    private static final transient Logger logger = LoggerFactory.getLogger(PrintAllBrandsTest.class);
    public static final int ITERATIONS_COUNT = 10;
    public static final int WARMUP_ITERATIONS_COUNT = 10;
    public static final int ITERATION_DURATION_MSEC = 1000;

    protected void performMeasurements(final Supplier functionToMeasure) {
        logger.info(format("================<{0}>================", functionToMeasure.getClass().getSimpleName()));

        warmup(functionToMeasure);
        final ArrayList<Long> statistics = benchmark(functionToMeasure);
        printStatistics(statistics);
    }

    private void printStatistics(final ArrayList<Long> statistics) {
        final StatisticalSummary descriptiveStatistics = new DescriptiveStatistics(Doubles.toArray(statistics));
        logger.info(format("Min elapsed time: {0}", descriptiveStatistics.getMin()));
        logger.info(format("Max elapsed time: {0}", descriptiveStatistics.getMax()));
        logger.info(format("Avg elapsed time: {0}", descriptiveStatistics.getMean()));
        logger.info(format("Standard deviation: {0}", descriptiveStatistics.getStandardDeviation()));
        logger.info(format("Confidence interval width: {0}", getConfidenceIntervalWidth(descriptiveStatistics, 0.95)));
    }

    private double getConfidenceIntervalWidth(final StatisticalSummary statisticalSummary, final double significance) {
        final TDistribution tDist = new TDistribution(statisticalSummary.getN() - 1);
        final double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
        return a * statisticalSummary.getStandardDeviation() / Math.sqrt(statisticalSummary.getN());
    }

    private void warmup(final Supplier functionToMeasure) {
        for (int i = 0; i < WARMUP_ITERATIONS_COUNT; i++) {
            performMeasurement(functionToMeasure);
        }
    }

    private ArrayList<Long> benchmark(final Supplier functionToMeasure) {
        final ArrayList<Long> measurements = Lists.newArrayList();

        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            long time = performMeasurement(functionToMeasure);
            logger.info("Iteration " + i + ": " + time);
            measurements.add(time);
        }
        return measurements;
    }

    private volatile boolean shouldStop;
    private Object result;

    private long performMeasurement(final Supplier toMeasure) {

        Thread finishThread = new Thread(new FinishNotifyTask());
        finishThread.start();

        final Stopwatch stopWatch = new Stopwatch();
        stopWatch.start();

        long count = 0;
        shouldStop = false;
        while (!shouldStop) {
            result = toMeasure.get();
            count++;
        }
        stopWatch.stop();

        try {
            finishThread.join();
        } catch (InterruptedException e) {
            // do nothing
        }

        return stopWatch.elapsedTime(TimeUnit.NANOSECONDS) / count;
    }

    private class FinishNotifyTask implements Runnable {

        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(ITERATION_DURATION_MSEC);
            } catch (InterruptedException e) {

            }
            shouldStop = true;
        }
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
