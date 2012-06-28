package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
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


    private class FindBuysOfYoungestPersonIterable implements Supplier<Void> {
        private final Db db;

        public FindBuysOfYoungestPersonIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            Person youngest = null;
            for (final Person person : db.getPersons()) {
                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
            }
            final List<Sale> buys = new ArrayList<Sale>();
            for (final Sale sale : db.getSales()) {
                if (sale.getBuyer().equals(youngest)) {
                    buys.add(sale);
                }
            }
            return null;
        }
    }

    private class FindBuysOfYoungestPersonLambdaJ implements Supplier<Void> {
        private final Db db;

        public FindBuysOfYoungestPersonLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> sales = select(db.getSales(), having(on(Sale.class).getBuyer(),
                    equalTo(selectMin(db.getPersons(), on(Person.class).getAge()))));
            return null;
        }
    }
}
