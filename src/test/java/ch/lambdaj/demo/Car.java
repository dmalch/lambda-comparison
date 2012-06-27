package ch.lambdaj.demo;

public class Car {

    private String brand;
    private String model;
    private int year;
    private double originalValue;

    protected Car() {
    }

    public Car(final String brand, final String model, final int year, final double originalValue) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.originalValue = originalValue;
    }

    public String getBrand() {
        return brand;
    }

    public String getBrand2() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public String getName() {
        return getBrand() + " " + getModel();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!Car.class.isInstance(obj)) return false;
        return getName().equals(((Car) obj).getName());
    }
}