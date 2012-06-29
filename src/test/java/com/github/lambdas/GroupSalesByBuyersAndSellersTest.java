package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import ch.lambdaj.group.Group;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

public class GroupSalesByBuyersAndSellersTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersIterable functionToMeasure = new GroupSalesByBuyersAndSellersIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testLambdaJ() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersLambdaJ functionToMeasure = new GroupSalesByBuyersAndSellersLambdaJ(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersJDKLambda functionToMeasure = new GroupSalesByBuyersAndSellersJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    private class GroupSalesByBuyersAndSellersIterable implements Supplier<Void> {
        private final Db db;

        public GroupSalesByBuyersAndSellersIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Map<Person, Map<Person, Sale>> map = new HashMap<>();
            for (final Sale sale : db.getSales()) {
                final Person buyer = sale.getBuyer();
                Map<Person, Sale> buyerMap = map.get(buyer);
                if (buyerMap == null) {
                    buyerMap = new HashMap<>();
                    map.put(buyer, buyerMap);
                }
                buyerMap.put(sale.getSeller(), sale);
            }
            Person youngest = null;
            Person oldest = null;
            for (final Person person : db.getPersons()) {
                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
                if (oldest == null || person.getAge() > oldest.getAge()) {
                    oldest = person;
                }
            }
            final Sale saleFromYoungestToOldest = map.get(youngest).get(oldest);
            return null;
        }
    }

    private class GroupSalesByBuyersAndSellersLambdaJ implements Supplier<Void> {
        private final Db db;

        public GroupSalesByBuyersAndSellersLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {
            final Group<Sale> group = group(db.getSales(), by(on(Sale.class).getBuyer()), by(on(Sale.class).getSeller()));
            final Person youngest = selectMin(db.getPersons(), on(Person.class).getAge());
            final Person oldest = selectMax(db.getPersons(), on(Person.class).getAge());
            final Sale sale = group.findGroup(youngest).find(oldest).get(0);
            return null;
        }
    }

    private class GroupSalesByBuyersAndSellersJDKLambda implements Supplier<Void> {
        private final Db db;

        public GroupSalesByBuyersAndSellersJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Void get() {

            final Map<Person, Iterable<Sale>> buyerToSale =
                    db.getSales()
                            .<Person>groupBy((Sale s)->s.getBuyer()).into(new HashMap<Person, Iterable<Sale>>());

            final Map<Person, Map<Person, Iterable<Sale>>> buyerToSellerToSale = buyerToSale
                    .<Map<Person, Iterable<Sale>>>mapValues((Iterable<Sale> s)->s.<Person>groupBy((Sale sale)->sale.getSeller()).into(new HashMap<Person, Iterable<Sale>>()))
                    .into(new HashMap<Person, Map<Person, Iterable<Sale>>>());

            final Iterable<BiValue<Person, Integer>> mapped = db.getPersons().<Integer>mapped((Person p)->p.getAge()).asIterable();
            final Person youngest = calcMin(mapped, (BiValue<Person, Integer> b1, BiValue<Person, Integer> b2)->Integer.compare(b1.getValue(), b2.getValue())).getKey();
            final Person oldest = calcMax(mapped, (BiValue<Person, Integer> b1, BiValue<Person, Integer> b2)->Integer.compare(b1.getValue(), b2.getValue())).getKey();

            final Sale sale = buyerToSellerToSale.get(youngest).get(oldest).getFirst();
            return null;
        }
    }
}
