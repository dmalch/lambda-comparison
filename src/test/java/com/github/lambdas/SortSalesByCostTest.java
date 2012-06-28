package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

public class SortSalesByCostTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final SortSalesByCostIterable functionToMeasure = new SortSalesByCostIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final SortSalesByCostLambdaJ functionToMeasure = new SortSalesByCostLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final SortSalesByCostJDKLambda functionToMeasure = new SortSalesByCostJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    private class SortSalesByCostIterable implements Supplier<Void> {
        private final Db db;

        public SortSalesByCostIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> sortedSales = new ArrayList<Sale>(db.getSales());
            Collections.sort(sortedSales, new Comparator<Sale>() {
                public int compare(final Sale s1, final Sale s2) {
                    return Double.valueOf(s1.getCost()).compareTo(s2.getCost());
                }
            });
            return null;
        }
    }

    private class SortSalesByCostLambdaJ implements Supplier<Void> {
        private final Db db;

        public SortSalesByCostLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> sortedSales = sort(db.getSales(), on(Sale.class).getCost());
            return null;
        }
    }

    private class SortSalesByCostJDKLambda implements Supplier<Void> {
        private final Db db;

        public SortSalesByCostJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final List<Sale> sortedSales = db.getSales().sorted((Sale s1, Sale s2)->Double.compare(s1.getCost(), s2.getCost())).into(new ArrayList<Sale>());
            return null;
        }
    }
}
