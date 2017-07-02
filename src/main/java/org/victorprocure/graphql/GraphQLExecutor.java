package org.victorprocure.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

public class GraphQLExecutor {

    @Resource
    private EntityManager entityManager;
    private GraphQL graphQL;

    private BiConsumer<GraphQLSchema.Builder, EntityManager> mutationSchema;
    private IDependencyResolver dependencyResolver;

    protected GraphQLExecutor() {}

    public GraphQLExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        createGraphQL();
    }

    public GraphQLExecutor(EntityManager entityManager, BiConsumer<GraphQLSchema.Builder, EntityManager> mutationSchema) {
        this.entityManager = entityManager;
        this.mutationSchema = mutationSchema;

        createGraphQL();
    }

    public GraphQLExecutor(EntityManager entityManager, BiConsumer<GraphQLSchema.Builder, EntityManager> mutationSchema, IDependencyResolver dependencyResolver) {
        this.entityManager = entityManager;
        this.mutationSchema = mutationSchema;
        this.dependencyResolver = dependencyResolver;

        createGraphQL();
    }

    @PostConstruct
    protected void createGraphQL() {
        if (entityManager != null) {
            GraphQLSchemaBuilder schemaBuilder;

            if(this.mutationSchema != null) {
                schemaBuilder = new GraphQLSchemaBuilder(entityManager, this.mutationSchema);
            }else{
                schemaBuilder = new GraphQLSchemaBuilder(entityManager);
            }

            this.graphQL = GraphQL
                    .newGraphQL(schemaBuilder.getGraphQLSchema())
                    .build();
        }
    }

    @Transactional
    public ExecutionResult execute(String query) {
        return this.execute(query, null);
    }

    @Transactional
    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        return this.execute(query, arguments, null);
    }

    @Transactional
    public ExecutionResult execute(String query, Map<String, Object> arguments, Object context) {
        if(arguments == null && context == null) {
            return graphQL.execute(query);
        }

        if(arguments == null) {
            arguments = Collections.<String, Object>emptyMap();
        }

        return graphQL.execute(query, context, arguments);
    }

    public void setDependencyResolver(IDependencyResolver dependencyResolver) {
        this.dependencyResolver = dependencyResolver;
    }

}
