package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import ch.lambdaj.group.Group;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.group.Groups.by;

public class FindMostBoughtCarTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarIterable functionToMeasure = new FindMostBoughtCarIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarLambdaJ functionToMeasure = new FindMostBoughtCarLambdaJ(db);

        performMeasurements(functionToMeasure);
    }


    private class FindMostBoughtCarIterable implements Supplier<Void> {
        private final Db db;

        public FindMostBoughtCarIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Map<Car, Integer> carsBought = new HashMap<Car, Integer>();
            for (final Sale sale : db.getSales()) {
                final Car car = sale.getCar();
                final Integer boughtTimes = carsBought.get(car);
                carsBought.put(car, boughtTimes == null ? 1 : boughtTimes + 1);
            }

            Car mostBoughtCarIterative = null;
            int boughtTimesIterative = 0;
            for (final Map.Entry<Car, Integer> entry : carsBought.entrySet()) {
                if (entry.getValue() > boughtTimesIterative) {
                    mostBoughtCarIterative = entry.getKey();
                    boughtTimesIterative = entry.getValue();
                }
            }
            return null;
        }
    }

    private class FindMostBoughtCarLambdaJ implements Supplier<Void> {
        private final Db db;

        public FindMostBoughtCarLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Group<Sale> group = selectMax(
                    group(db.getSales(), by(on(Sale.class).getCar())).subgroups(), on(Group.class).getSize());
            final Car mostBoughtCar = group.findAll().get(0).getCar();
            final int boughtTimes = group.getSize();
            return null;
        }
    }
}
