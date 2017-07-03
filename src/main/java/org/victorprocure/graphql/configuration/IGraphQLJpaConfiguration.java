package org.victorprocure.graphql.configuration;

import org.victorprocure.graphql.IDependencyResolver;
import org.victorprocure.graphql.mutations.IMutationProvider;
import org.victorprocure.graphql.querying.IQueryProvider;
import org.victorprocure.graphql.schema.ISchemaBuilder;

import javax.persistence.EntityManager;

/**
 * Created by victo on 7/2/2017.
 */
public interface IGraphQLJpaConfiguration {
    boolean getAllowMutations();

    boolean getAllowQuery();

    IDependencyResolver getDependencyResolver();

    EntityManager getEntityManager();

    IMutationProvider getMutationProvider();

    IQueryProvider getQueryProvider();

    ISchemaBuilder getSchemaBuilder();
}
