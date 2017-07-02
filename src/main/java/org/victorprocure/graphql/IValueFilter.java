package org.victorprocure.graphql;

/**
 * Created by victo on 7/2/2017.
 */
public interface IValueFilter<T> {
    public T applyFilter(T value);
}
