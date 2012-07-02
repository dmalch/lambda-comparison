package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.Collection;

import static ch.lambdaj.Lambda.*;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

public class SumCostsWhereBothAreMalesTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesIterable functionToMeasure = new SumCostsWhereBothAreMalesIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesLambdaJ functionToMeasure = new SumCostsWhereBothAreMalesLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesJDKLambda functionToMeasure = new SumCostsWhereBothAreMalesJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesGuava functionToMeasure = new SumCostsWhereBothAreMalesGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class SumCostsWhereBothAreMalesIterable implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            double sum = 0.0;
            for (final Sale sale : db.getSales()) {
                if (sale.getBuyer().isMale() && sale.getSeller().isMale()) {
                    sum += sale.getCost();
                }
            }
            return sum;
        }
    }

    private class SumCostsWhereBothAreMalesLambdaJ implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            final double sum = sumFrom(select(db.getSales(),
                    having(on(Sale.class).getBuyer().isMale())
                            .and(having(on(Sale.class).getSeller().isMale())))).getCost();
            return sum;
        }
    }

    private class SumCostsWhereBothAreMalesJDKLambda implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            final Double sum = db.getSales()
                    .filter((Sale s)->s.getBuyer().isMale() && s.getSeller().isMale())
                    .<Double>mapReduce((Sale s)->s.getCost(), 0.0, (Double d1, Double d2)->d1 + d2);
            return sum;
        }
    }

    private class SumCostsWhereBothAreMalesGuava implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {

            final Collection<Double> transform = transform(filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getBuyer().isMale() && input.getSeller().isMale();
                }
            }), new Function<Sale, Double>() {
                @Override
                public Double apply(final Sale input) {
                    return input.getCost();
                }
            });

            Double sum = 0.0;
            for (final Double aDouble : transform) {
                sum += aDouble;
            }
            return sum;
        }
    }
}
