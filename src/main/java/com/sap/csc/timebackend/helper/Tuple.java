package com.sap.csc.timebackend.helper;

import java.io.Serializable;

public class Tuple<U, V> implements Serializable {

    private static final long serialVersionUID = -665282590558106119L;
    private final U first;
    private final V second;

    private Tuple(U first, V second) {
        this.first = first;
        this.second = second;
    }

    private Tuple() {
        this.first = null;
        this.second = null;
    }

    public static <U, V> Tuple<U, V> create(U first, V second) {
        return new Tuple<>(first, second);
    }

    public U getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }


    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        return (first != null ? first.equals(tuple.first) : tuple.first == null) && (second != null ? second.equals(tuple.second) : tuple.second == null);
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}