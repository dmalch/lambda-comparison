package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static ch.lambdaj.Lambda.*;
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

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable implements Supplier<Void> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            int age = Integer.MAX_VALUE;
            for (final Sale sale : db.getSales()) {
                if (sale.getCost() > 50000.00) {
                    final int buyerAge = sale.getBuyer().getAge();
                    if (buyerAge < age) {
                        age = buyerAge;
                    }
                }
            }
            return null;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000LambdaJ implements Supplier<Void> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000LambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final int age = min(forEach(select(db.getSales(), having(on(Sale.class).getCost(),
                    greaterThan(50000.00)))).getBuyer(), on(Person.class).getAge());
            return null;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda implements Supplier<Void> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final int age = Collections.min(db.getSales()
                    .filter((Sale sale)->sale.getCost() > 50000.00)
                    .<Integer>map((Sale sale)->sale.getBuyer().getAge())
                    .into(new ArrayList<Integer>()));
            return null;
        }
    }
}
