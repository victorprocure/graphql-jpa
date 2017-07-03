package org.victorprocure.graphql.configuration;

import org.victorprocure.graphql.IDependencyResolver;
import org.victorprocure.graphql.mutations.IMutationProvider;
import org.victorprocure.graphql.mutations.MutationProvider;
import org.victorprocure.graphql.querying.IQueryProvider;
import org.victorprocure.graphql.querying.QueryProvider;
import org.victorprocure.graphql.schema.GraphQLJpaSchemaBuilder;
import org.victorprocure.graphql.schema.ISchemaBuilder;

import javax.persistence.EntityManager;

/**
 * Created by victo on 7/2/2017.
 */
@SuppressWarnings("unused")
public class GraphQLJpaConfigurer {
    private boolean allowMutations = false;
    private boolean allowQuery = true;

    private IDependencyResolver dependencyResolver;
    private EntityManager entityManager;

    private IMutationProvider mutationProvider;
    private IQueryProvider queryProvider;

    private ISchemaBuilder schemaBuilder;

    private GraphQLJpaConfigurer(){}

    public static GraphQLJpaConfigurer newConfiguration() {
        return new GraphQLJpaConfigurer();
    }

    public GraphQLJpaConfigurer canMutate(boolean allowMutations) {
        this.allowMutations = allowMutations;

        return this;
    }

    public GraphQLJpaConfigurer customMutationProvider(IMutationProvider mutationProvider) {
        this.mutationProvider = mutationProvider;

        return this;
    }

    public GraphQLJpaConfigurer canQuery(boolean allowQuery) {
        this.allowQuery = allowQuery;

        return this;
    }

    public GraphQLJpaConfigurer customQueryProvider(IQueryProvider queryProvider) {
        this.queryProvider = queryProvider;
        return this;
    }

    public GraphQLJpaConfigurer setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;

        return this;
    }

    public GraphQLJpaConfigurer setDependencyResolver(IDependencyResolver dependencyResolver) {
        this.dependencyResolver = dependencyResolver;

        return this;
    }

    public GraphQLJpaConfigurer customSchemaBuilder(ISchemaBuilder schemaBuilder) {
        this.schemaBuilder = schemaBuilder;

        return this;
    }

    public IGraphQLJpaConfiguration buildConfiguration() {
        if(this.entityManager == null) {
            throw new NullPointerException("No entity manager provided");
        }

        if(!this.allowQuery && !this.allowMutations) {
            throw new IllegalArgumentException("No Graph to build, mutations and queries disabled");
        }

        if(this.allowQuery  && this.queryProvider == null) {
            this.queryProvider = new QueryProvider();
        }

        if(this.allowMutations && this.mutationProvider == null) {
            this.mutationProvider = new MutationProvider();
        }

        if(this.schemaBuilder == null) {
            this.schemaBuilder = new GraphQLJpaSchemaBuilder(this.entityManager);
        }

        GraphQLJpaConfiguration configuration =
                new GraphQLJpaConfiguration(
                        this.allowMutations,
                        this.allowQuery,
                        this.dependencyResolver,
                        this.entityManager,
                        this.mutationProvider,
                        this.queryProvider,
                        this.schemaBuilder);

        return configuration;
    }
}
