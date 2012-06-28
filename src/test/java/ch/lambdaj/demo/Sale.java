package ch.lambdaj.demo;


public class Sale {

    private Person seller;
    private Person buyer;
    private Car car;
    private double cost;

    protected Sale() { }

    public Sale(final Person seller, final Person buyer, final Car car, final double cost) {
        this.seller = seller;
        this.buyer = buyer;
        this.car = car;
        this.cost = cost;
    }

    public Person getSeller() {
        return seller;
    }

    public Person getBuyer() {
        return buyer;
    }

    public Car getCar() {
        return car;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Sale of " + car + " from " + seller + " to " + buyer + " for " + cost;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!Sale.class.isInstance(obj)) return false;
        return toString().equals(obj.toString());
    }
}