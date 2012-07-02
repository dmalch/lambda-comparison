package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import ch.lambdaj.group.Group;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectMax;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.max;

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

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarJDKLambda functionToMeasure = new FindMostBoughtCarJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarGuava functionToMeasure = new FindMostBoughtCarGuava(db);

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

    private class FindMostBoughtCarLambdaJ implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Group<Sale> group = selectMax(
                    group(db.getSales(), by(on(Sale.class).getCar())).subgroups(), on(Group.class).getSize());
            final Car mostBoughtCar = group.findAll().get(0).getCar();
            return mostBoughtCar;
        }
    }

    private class FindMostBoughtCarJDKLambda implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Iterable<Sale> max = calcMax(db.getSales().groupBy((Sale s)->s.getCar()).values(),
                    (final Iterable<Sale> o1, final Iterable<Sale> o2)->Long.compare(o1.count(), o2.count()));

            final Car mostBoughtCar = max.getFirst().getCar();
            return mostBoughtCar;
        }
    }

    private class FindMostBoughtCarGuava implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Collection<Sale> max = max(index(db.getSales(), new Function<Sale, Car>() {
                @Override
                public Car apply(final Sale input) {
                    return input.getCar();
                }
            }).asMap().values(), new Comparator<Collection<Sale>>() {
                @Override
                public int compare(final Collection<Sale> o1, final Collection<Sale> o2) {
                    return Long.compare(size(o1), size(o2));
                }
            });
            final Car mostBoughtCar = getFirst(max, null).getCar();
            return mostBoughtCar;
        }
    }
}
