package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Supplier;
import org.junit.Test;

import static ch.lambdaj.Lambda.*;

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


    private class SumCostsWhereBothAreMalesIterable implements Supplier<Void> {
        private final Db db;

        public SumCostsWhereBothAreMalesIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            double sum = 0.0;
            for (final Sale sale : db.getSales()) {
                if (sale.getBuyer().isMale() && sale.getSeller().isMale())
                    sum += sale.getCost();
            }
            return null;
        }
    }

    private class SumCostsWhereBothAreMalesLambdaJ implements Supplier<Void> {
        private final Db db;

        public SumCostsWhereBothAreMalesLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final double sum = sumFrom(select(db.getSales(),
                    having(on(Sale.class).getBuyer().isMale())
                            .and(having(on(Sale.class).getSeller().isMale())))).getCost();
            return null;
        }
    }
}