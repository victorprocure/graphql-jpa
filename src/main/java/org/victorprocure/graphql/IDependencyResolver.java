package org.victorprocure.graphql; 

public interface IDependencyResolver {
    Class<?> resolve(String dependencyName);

    Class<?> resolve(Object dependencyObject);
}