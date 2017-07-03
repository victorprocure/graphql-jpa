package org.victorprocure.graphql.schema;

import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.victorprocure.graphql.configuration.IGraphQLJpaConfiguration;
import org.victorprocure.graphql.strategy.JpaResolverBuilder;

import javax.persistence.EntityManager;

/**
 * Created by victo on 7/2/2017.
 */
public class GraphQLJpaSchemaBuilder implements ISchemaBuilder {
    private EntityManager entityManager;

    public GraphQLJpaSchemaBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public GraphQLSchema buildSchema() {
        GraphQLSchemaGenerator generator = new GraphQLSchemaGenerator();
        this.entityManager.getMetamodel().getEntities().stream()
                .forEach(e->generator.withOperationsFromType(e.getJavaType()));
        generator.withResolverBuilders(new JpaResolverBuilder());
        GraphQLSchema schema = generator.generate();

        return schema;
    }
}
