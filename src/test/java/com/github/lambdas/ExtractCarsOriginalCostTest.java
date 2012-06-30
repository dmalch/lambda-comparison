package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.google.common.collect.Lists.transform;

public class ExtractCarsOriginalCostTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostIterable functionToMeasure = new ExtractCarsOriginalCostIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostLambdaJ functionToMeasure = new ExtractCarsOriginalCostLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostJDKLambda functionToMeasure = new ExtractCarsOriginalCostJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaWithNewList() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostJDKLambdaWithListCreation functionToMeasure = new ExtractCarsOriginalCostJDKLambdaWithListCreation(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostGuava functionToMeasure = new ExtractCarsOriginalCostGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class ExtractCarsOriginalCostIterable implements Supplier<Void> {
        private final Db db;

        public ExtractCarsOriginalCostIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Double> costs = new ArrayList<Double>();
            for (final Car car : db.getCars()) {
                costs.add(car.getOriginalValue());
            }
            return null;
        }
    }

    private class ExtractCarsOriginalCostLambdaJ implements Supplier<Void> {
        private final Db db;

        public ExtractCarsOriginalCostLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Double> costs = extract(db.getCars(), on(Car.class).getOriginalValue());
            return null;
        }
    }

    private class ExtractCarsOriginalCostJDKLambda implements Supplier<Void> {
        private final Db db;

        public ExtractCarsOriginalCostJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Iterable<Double> costs = db.getCars().map((Car c)->c.getOriginalValue());
            return null;
        }
    }

    private class ExtractCarsOriginalCostJDKLambdaWithListCreation implements Supplier<Void> {
        private final Db db;

        public ExtractCarsOriginalCostJDKLambdaWithListCreation(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Double> costs = db.getCars()
                    .<Double>map((Car c)->c.getOriginalValue())
                    .into(new ArrayList<Double>());
            return null;
        }
    }

    private class ExtractCarsOriginalCostGuava implements Supplier<Void> {
        private final Db db;

        public ExtractCarsOriginalCostGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Double> costs = Lists.newArrayList(transform(db.getCars(), new Function<Car, Double>() {
                @Override
                public Double apply(final Car input) {
                    return input.getOriginalValue();
                }
            }));

            return null;
        }
    }
}
