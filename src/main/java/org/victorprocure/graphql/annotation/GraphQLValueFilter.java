package org.victorprocure.graphql.annotation;

import org.victorprocure.graphql.IValueFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by victo on 7/2/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GraphQLValueFilter {
    Class<? extends IValueFilter<?>> filter();
}
