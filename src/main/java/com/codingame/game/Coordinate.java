package com.codingame.game;

import java.util.Objects;

/**
 * Class for storing information about a coordinate.
 */
public class Coordinate {

    private final int y;
    private final int x;
    // Colour identifier number.
    private char number;

    public Coordinate(int y,int x){
        this.y = y;
        this.x = x;
    }

    public Coordinate(int y,int x, char number){
        this.y = y;
        this.x = x;
        this.number = number;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public char getNumber() {
        return number;
    }

    /**
     * Custom checker for comparing coordinates based on position ONLY.
     * @param o - Coordinate to compare to.
     * @return Boolean - True if the same position otherwise False.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return y == that.y &&
                x == that.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "y=" + y +
                ", x=" + x +
                ", number=" + number +
                '}';
    }
}
