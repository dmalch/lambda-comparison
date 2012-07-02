package com.github.lambdas;

import ch.lambdaj.Lambda;
import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.StringJoiner;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Collections2.transform;

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

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsGuava functionToMeasure = new PrintAllBrandsGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class PrintAllBrandsIterable implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsIterable(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final StringBuilder sb = new StringBuilder();
            for (final Car car : db.getCars()) {
                sb.append(car.getBrand()).append(", ");
            }
            final String brands = sb.toString().substring(0, sb.length() - 2);
            return brands;
        }
    }

    private class PrintAllBrandsLambdaJ implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final String brands = Lambda.joinFrom(db.getCars()).getBrand();
            return brands;
        }
    }

    private class PrintAllBrandsJDKLambda implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final StringJoiner stringJoiner = db.getCars().<String>map((Car c)->c.getBrand()).into(new StringJoiner(","));
            final String brands = stringJoiner.toString();
            return brands;
        }
    }

    private class PrintAllBrandsGuava implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsGuava(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final String brands = on(",").join(transform(db.getCars(), new Function<Car, String>() {
                @Override
                public String apply(final Car input) {
                    return input.getBrand();
                }
            }));
            return brands;
        }
    }
}
