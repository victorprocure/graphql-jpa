package org.victorprocure.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.victorprocure.graphql.configuration.IGraphQLJpaConfiguration;
import org.victorprocure.graphql.schema.GraphQLSchemaBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

public class GraphQLExecutor {
    private GraphQL graphQL;

    private IGraphQLJpaConfiguration configuration;

    protected GraphQLExecutor() {}

    public GraphQLExecutor(IGraphQLJpaConfiguration configuration) {
     this.configuration = configuration;

     this.createGraphQL();
    }


    @PostConstruct
    protected void createGraphQL() {
        this.graphQL = GraphQL
                .newGraphQL(this.configuration.getSchemaBuilder().buildSchema())
                .build();
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
}
