package com.myuser.ugcampusroutefinder.model;

import java.util.Objects;

// Using a class instead of a record to allow for identity-based equality
// in some graph algorithms, though for this implementation, value-based equality is fine.
public class Location {
    private final String name;

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
