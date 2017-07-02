package org.victorprocure.graphql.annotation;

import org.victorprocure.graphql.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by victor on 7/1/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface GraphQLConstraint {
  OperationType operationType();
  String matches();
  int equals();
}

