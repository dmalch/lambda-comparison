package ch.lambdaj.demo;

import java.util.Date;

import static ch.lambdaj.demo.Util.*;

public class Person {

    private String firstName;
    private String lastName;
    private boolean male;
    private Date birthday;

    protected Person() {
    }

    public Person(final String firstName, final String lastName, final boolean male, final String birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.male = male;
        this.birthday = formatDate(birthday);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public boolean isMale() {
        return male;
    }

    public int getAge() {
        return getCurrentYear() - getBirthYear();
    }

    public int getBirthYear() {
        return getYear(birthday);
    }

    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(final Object obj) {
        return Person.class.isInstance(obj) && getFullName().equals(((Person) obj).getFullName());
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }
}