package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import ch.lambdaj.group.Group;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectMax;
import static ch.lambdaj.Lambda.selectMin;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.max;
import static java.util.Collections.min;

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

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersGuava functionToMeasure = new GroupSalesByBuyersAndSellersGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class GroupSalesByBuyersAndSellersIterable implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {
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
            return saleFromYoungestToOldest;
        }
    }

    private class GroupSalesByBuyersAndSellersLambdaJ implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersLambdaJ(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {
            final Group<Sale> group = group(db.getSales(), by(on(Sale.class).getBuyer()), by(on(Sale.class).getSeller()));
            final Person youngest = selectMin(db.getPersons(), on(Person.class).getAge());
            final Person oldest = selectMax(db.getPersons(), on(Person.class).getAge());
            final Sale saleFromYoungestToOldest = group.findGroup(youngest).find(oldest).get(0);
            return saleFromYoungestToOldest;
        }
    }

    private class GroupSalesByBuyersAndSellersJDKLambda implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {

            final Map<Person, Iterable<Sale>> buyerToSale =
                    db.getSales()
                            .<Person>groupBy((Sale s)->s.getBuyer()).into(new HashMap<Person, Iterable<Sale>>());

            final Map<Person, Map<Person, Iterable<Sale>>> buyerToSellerToSale = buyerToSale
                    .<Map<Person, Iterable<Sale>>>mapValues((Iterable<Sale> s)->s.<Person>groupBy((Sale sale)->sale.getSeller()).into(new HashMap<Person, Iterable<Sale>>()))
                    .into(new HashMap<Person, Map<Person, Iterable<Sale>>>());

            final Iterable<BiValue<Person, Integer>> mapped = db.getPersons().<Integer>mapped((Person p)->p.getAge()).asIterable();
            final Person youngest = calcMin(mapped, (BiValue<Person, Integer> b1, BiValue<Person, Integer> b2)->Integer.compare(b1.getValue(), b2.getValue())).getKey();
            final Person oldest = calcMax(mapped, (BiValue<Person, Integer> b1, BiValue<Person, Integer> b2)->Integer.compare(b1.getValue(), b2.getValue())).getKey();

            final Sale saleFromYoungestToOldest = buyerToSellerToSale.get(youngest).get(oldest).getFirst();
            return saleFromYoungestToOldest;
        }
    }

    private class GroupSalesByBuyersAndSellersGuava implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {
            final ImmutableMap<Person, Collection<Sale>> buyerToSale = index(db.getSales(), new Function<Sale, Person>() {
                @Override
                public Person apply(final Sale input) {
                    return input.getBuyer();
                }
            }).asMap();
            final Map<Person, Map<Person, Collection<Sale>>> buyerToSellerToSale = Maps.transformValues(buyerToSale, new Function<Collection<Sale>, Map<Person, Collection<Sale>>>() {
                @Override
                public Map<Person, Collection<Sale>> apply(final Collection<Sale> input) {
                    return index(input, new Function<Sale, Person>() {
                        @Override
                        public Person apply(final Sale input) {
                            return input.getSeller();
                        }
                    }).asMap();
                }
            });

            final ImmutableMap<Integer, Person> mapped = uniqueIndex(db.getPersons(), new Function<Person, Integer>() {
                @Override
                public Integer apply(final Person input) {
                    return input.getAge();
                }
            });
            final Person youngest = min(mapped.entrySet(), new Comparator<Map.Entry<Integer, Person>>() {
                @Override
                public int compare(final Map.Entry<Integer, Person> o1, final Map.Entry<Integer, Person> o2) {
                    return Integer.compare(o1.getKey(), o2.getKey());
                }
            }).getValue();
            final Person oldest = max(mapped.entrySet(), new Comparator<Map.Entry<Integer, Person>>() {
                @Override
                public int compare(final Map.Entry<Integer, Person> o1, final Map.Entry<Integer, Person> o2) {
                    return Integer.compare(o1.getKey(), o2.getKey());
                }
            }).getValue();

            final Sale saleFromYoungestToOldest = Iterables.getFirst(buyerToSellerToSale.get(youngest).get(oldest), null);
            return saleFromYoungestToOldest;
        }
    }
}
