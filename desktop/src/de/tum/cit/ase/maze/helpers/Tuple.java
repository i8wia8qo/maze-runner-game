package de.tum.cit.ase.maze.helpers;

import java.util.Comparator;
import java.util.Objects;

public class Tuple{

    int x;
    int y;

    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tuple otherTuple = (Tuple) obj;
        return x == otherTuple.x && y == otherTuple.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
