package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.min;
import static org.hamcrest.CoreMatchers.equalTo;

public class FindBuysOfYoungestPersonTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonIterable functionToMeasure = new FindBuysOfYoungestPersonIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonLambdaJ functionToMeasure = new FindBuysOfYoungestPersonLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonJDKLambda functionToMeasure = new FindBuysOfYoungestPersonJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonGuava functionToMeasure = new FindBuysOfYoungestPersonGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindBuysOfYoungestPersonIterable implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonIterable(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            Person youngest = null;
            for (final Person person : db.getPersons()) {
                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
            }
            final List<Sale> buys = new ArrayList<>();
            for (final Sale sale : db.getSales()) {
                if (sale.getBuyer().equals(youngest)) {
                    buys.add(sale);
                }
            }
            return buys;
        }
    }

    private class FindBuysOfYoungestPersonLambdaJ implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final List<Sale> buys = select(db.getSales(), having(on(Sale.class).getBuyer(),
                    equalTo(selectMin(db.getPersons(), on(Person.class).getAge()))));
            return buys;
        }
    }

    private class FindBuysOfYoungestPersonJDKLambda implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final Person min = min(db.getPersons(), (Person p1, Person p2)->Integer.compare(p1.getAge(), p2.getAge()));
            final List<Sale> buys = db.getSales()
                    .filter((Sale s)->s.getBuyer().equals(min))
                    .into(new ArrayList<Sale>());
            return buys;
        }
    }

    private class FindBuysOfYoungestPersonGuava implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonGuava(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final Person min = min(db.getPersons(), new Comparator<Person>() {
                @Override
                public int compare(final Person o1, final Person o2) {
                    return Integer.compare(o1.getAge(), o2.getAge());
                }
            });
            final List<Sale> buys = newArrayList(filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getBuyer().equals(min);
                }
            }));

            return buys;
        }
    }
}
