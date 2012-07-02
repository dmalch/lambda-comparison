package com.github.lambdas;

import ch.lambdaj.Lambda;
import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;

import static ch.lambdaj.Lambda.forEach;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.min;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public class FindAgeOfYoungestWhoBoughtForMoreThan50000Test extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000LambdaJ functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000LambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000Guava functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000Guava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            int age = Integer.MAX_VALUE;
            for (final Sale sale : db.getSales()) {
                if (sale.getCost() > 50000.00) {
                    final int buyerAge = sale.getBuyer().getAge();
                    if (buyerAge < age) {
                        age = buyerAge;
                    }
                }
            }
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000LambdaJ implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000LambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = Lambda.min(forEach(select(db.getSales(), having(on(Sale.class).getCost(),
                    greaterThan(50000.00)))).getBuyer(), on(Person.class).getAge());
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = min(db.getSales()
                    .filter((Sale sale)->sale.getCost() > 50000.00)
                    .<Integer>map((Sale sale)->sale.getBuyer().getAge())
                    .into(new ArrayList<Integer>()));
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000Guava implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000Guava(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = min(transform(filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getCost() > 50000.00;
                }
            }), new Function<Sale, Integer>() {
                @Override
                public Integer apply(final Sale input) {
                    return input.getBuyer().getAge();
                }
            }));
            return age;
        }
    }
}
