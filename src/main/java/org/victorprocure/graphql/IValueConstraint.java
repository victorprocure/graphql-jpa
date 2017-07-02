package org.victorprocure.graphql;

/**
 * Created by victo on 7/2/2017.
 */
public interface IValueConstraint<T> {
    boolean matches(T value);
}
