package org.victorprocure.graphql; 

public interface IDependencyResolver {
    Object resolve(String dependencyName);

    Object resolve(Object dependencyObject);
}