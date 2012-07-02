package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;

public class IndexCarsByBrandTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandIterable functionToMeasure = new IndexCarsByBrandIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandLambdaJ functionToMeasure = new IndexCarsByBrandLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandJDKLambda functionToMeasure = new IndexCarsByBrandJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandGuava functionToMeasure = new IndexCarsByBrandGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class IndexCarsByBrandIterable implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = new HashMap<String, Car>();
            for (final Car car : db.getCars()) {
                carsByBrand.put(car.getBrand(), car);
            }
            return carsByBrand;
        }
    }

    private class IndexCarsByBrandLambdaJ implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = index(db.getCars(), on(Car.class).getBrand());
            return carsByBrand;
        }
    }

    private class IndexCarsByBrandJDKLambda implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = db.getCars().<String>mapped((Car c)->c.getBrand()).swap().into(new HashMap<String, Car>());
            return carsByBrand;
        }
    }

    private class IndexCarsByBrandGuava implements Supplier<ImmutableListMultimap<String, Car>> {
        private final Db db;

        public IndexCarsByBrandGuava(final Db db) {
            this.db = db;
        }

        @Override
        public ImmutableListMultimap<String, Car> get() {
            final ImmutableListMultimap<String, Car> carsByBrand = Multimaps.index(db.getCars(), new Function<Car, String>() {
                @Override
                public String apply(final Car input) {
                    return input.getBrand();
                }
            });
            return carsByBrand;
        }
    }
}
