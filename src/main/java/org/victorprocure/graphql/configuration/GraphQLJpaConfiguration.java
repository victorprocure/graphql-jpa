package org.victorprocure.graphql.configuration;

import org.victorprocure.graphql.IDependencyResolver;
import org.victorprocure.graphql.mutations.IMutationProvider;
import org.victorprocure.graphql.querying.IQueryProvider;
import org.victorprocure.graphql.schema.ISchemaBuilder;

import javax.persistence.EntityManager;

/**
 * Created by victo on 7/2/2017.
 */
class GraphQLJpaConfiguration implements IGraphQLJpaConfiguration {
    private boolean allowMutations;
    private boolean allowQuery;
    private IDependencyResolver dependencyResolver;
    private EntityManager entityManager;
    private IMutationProvider mutationProvider;
    private IQueryProvider queryProvider;
    private ISchemaBuilder schemaBuilder;

    public GraphQLJpaConfiguration(boolean allowMutations,
                                   boolean allowQuery,
                                   IDependencyResolver dependencyResolver,
                                   EntityManager entityManager,
                                   IMutationProvider mutationProvider,
                                   IQueryProvider queryProvider,
                                   ISchemaBuilder schemaBuilder) {
        this.allowMutations = allowMutations;
        this.allowQuery = allowQuery;
        this.dependencyResolver = dependencyResolver;
        this.entityManager = entityManager;
        this.mutationProvider = mutationProvider;
        this.queryProvider = queryProvider;
        this.schemaBuilder = schemaBuilder;
    }

    @Override
    public boolean getAllowMutations() {
        return this.allowMutations;
    }

    @Override
    public boolean getAllowQuery() {
        return this.allowQuery;
    }

    @Override
    public IDependencyResolver getDependencyResolver() {
        return this.dependencyResolver;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    public IMutationProvider getMutationProvider() {
        return this.mutationProvider;
    }

    @Override
    public IQueryProvider getQueryProvider() {
        return this.queryProvider;
    }

    @Override
    public ISchemaBuilder getSchemaBuilder() {
        return this.schemaBuilder;
    }
}
