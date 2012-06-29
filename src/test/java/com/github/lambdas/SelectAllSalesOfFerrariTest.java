package com.github.lambdas;

import ch.lambdaj.Lambda;
import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;

public class SelectAllSalesOfFerrariTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariIterable functionToMeasure = new SelectAllSalesOfFerrariIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariLambdaJ functionToMeasure = new SelectAllSalesOfFerrariLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariJDKLambda functionToMeasure = new SelectAllSalesOfFerrariJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariGuava functionToMeasure = new SelectAllSalesOfFerrariGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class SelectAllSalesOfFerrariIterable implements Supplier<Void> {
        private final Db db;

        public SelectAllSalesOfFerrariIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> salesOfAFerrari = new ArrayList<Sale>();
            for (final Sale sale : db.getSales()) {
                if (sale.getCar().getBrand().equals("Ferrari"))
                    salesOfAFerrari.add(sale);
            }
            return null;
        }
    }

    private class SelectAllSalesOfFerrariLambdaJ implements Supplier<Void> {
        private final Db db;

        public SelectAllSalesOfFerrariLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> salesOfAFerrari = Lambda.select(db.getSales(),
                    having(on(Sale.class).getCar().getBrand(), equalTo("Ferrari")));
            return null;
        }
    }

    private class SelectAllSalesOfFerrariJDKLambda implements Supplier<Void> {
        private final Db db;

        public SelectAllSalesOfFerrariJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> salesOfAFerrari = db.getSales().filter((Sale s)->s.getCar().getBrand().equals("Ferrari")).into(new ArrayList<Sale>());
            return null;
        }
    }

    private class SelectAllSalesOfFerrariGuava implements Supplier<Void> {
        private final Db db;

        public SelectAllSalesOfFerrariGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> salesOfAFerrari = newArrayList(filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getCar().getBrand().equals("Ferrari");
                }
            }));
            return null;
        }
    }
}
