package com.github.lambdas;

import ch.lambdaj.Lambda;
import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Supplier;
import org.junit.Test;

public class PrintAllBrandsTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsIterable functionToMeasure = new PrintAllBrandsIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsLambdaJ functionToMeasure = new PrintAllBrandsLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsJDKLambda functionToMeasure = new PrintAllBrandsJDKLambda(db);

        performMeasurements(functionToMeasure);
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

    private class PrintAllBrandsJDKLambda implements Supplier<Void> {
        private final Db db;

        public PrintAllBrandsJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            return null;
        }
    }
}
