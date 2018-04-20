package com.company.movement;

/**
 * Generic version of the Result class.
 * @param <T> the type of the value being boxed
 */
public class Result<T> {
    // T stands for "Type"
    private T t;

    public Result(T t) {
        this.t = t;
    }

    public void set(T t) { this.t = t; }
    public T get() { return t; }
}
