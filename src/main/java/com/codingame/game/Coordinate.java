package com.codingame.game;

import java.util.Objects;

public class Coordinate {

    private int y;
    private int x;
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
