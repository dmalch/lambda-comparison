package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.Collections;

import static ch.lambdaj.Lambda.max;
import static ch.lambdaj.Lambda.on;
import static com.google.common.collect.Collections2.transform;

public class FindMostCostlySaleTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleIterable functionToMeasure = new FindMostCostlySaleIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleLambdaJ functionToMeasure = new FindMostCostlySaleLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleJDKLambda functionToMeasure = new FindMostCostlySaleJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleGuava functionToMeasure = new FindMostCostlySaleGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindMostCostlySaleIterable implements Supplier<Void> {
        private final Db db;

        public FindMostCostlySaleIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            double maxCost = 0.0;
            for (final Sale sale : db.getSales()) {
                final double cost = sale.getCost();
                if (cost > maxCost) maxCost = cost;
            }
            return null;
        }
    }

    private class FindMostCostlySaleLambdaJ implements Supplier<Void> {
        private final Db db;

        public FindMostCostlySaleLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final double maxCost = max(db.getSales(), on(Sale.class).getCost());
            return null;
        }
    }

    private class FindMostCostlySaleJDKLambda implements Supplier<Void> {
        private final Db db;

        public FindMostCostlySaleJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Double maxCost = calcMax(db.getSales().<Double>map((Sale s)->s.getCost()));
            return null;
        }
    }

    private class FindMostCostlySaleGuava implements Supplier<Void> {
        private final Db db;

        public FindMostCostlySaleGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Double maxCost = Collections.max(transform(db.getSales(), new Function<Sale, Double>() {
                @Override
                public Double apply(final Sale input) {
                    return input.getCost();
                }
            }));

            return null;
        }
    }
}
