package org.victorprocure.graphql.schema;

import graphql.Scalars;
import graphql.schema.*;
import org.victorprocure.graphql.ExtendedJpaDataFetcher;
import org.victorprocure.graphql.JpaDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victorprocure.graphql.utils.SchemaUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.victorprocure.graphql.utils.SchemaUtils.getSchemaDocumentation;

public class GraphQLSchemaBuilder implements ISchemaBuilder {

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginationRequest";
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

    private EntityManager entityManager;
    private BiConsumer<GraphQLSchema.Builder, EntityManager> mutationSchema;

    public GraphQLSchemaBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public GraphQLSchemaBuilder(EntityManager entityManager, BiConsumer<GraphQLSchema.Builder, EntityManager> mutationSchema) {
        this.entityManager = entityManager;
        this.mutationSchema = mutationSchema;
    }

    public GraphQLSchema buildSchema() {
        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();
        schemaBuilder.query(getQueryType());

        if(this.mutationSchema != null) {
            this.mutationSchema.accept(schemaBuilder, this.entityManager);
        }

        return schemaBuilder.build();
    }

    private GraphQLObjectType getQueryType() {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("QueryType_JPA").description("All encompassing schema for this JPA environment");
        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(SchemaUtils::isNotIgnored).map(this::getQueryFieldDefinition).collect(Collectors.toList()));
        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(SchemaUtils::isNotIgnored).map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));

        return queryType.build();
    }

    private GraphQLFieldDefinition getQueryFieldDefinition(EntityType<?> entityType) {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.getName())
                .description(getSchemaDocumentation( entityType.getJavaType()))
                .type(new GraphQLList(SchemaUtils.getObjectType(entityType)))
                .dataFetcher(new JpaDataFetcher(entityManager, entityType))
                .argument(entityType.getAttributes().stream().filter(SchemaUtils::isValidInput).filter(SchemaUtils::isNotIgnored).map(SchemaUtils::getArgument).collect(Collectors.toList()))
                .build();
    }

    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = GraphQLObjectType.newObject()
                .name(entityType.getName() + "Connection")
                .description("'Connection' response wrapper object for " + entityType.getName() + ".  When pagination or aggregation is requested, this object will be returned with metadata about the query.")
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalPages").description("Total number of pages calculated on the database for this pageSize.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalElements").description("Total number of results on the database for this query.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("content").description("The actual object results").type(new GraphQLList(SchemaUtils.getObjectType(entityType))).build())
                .build();

        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.getName() + "Connection")
                .description("'Connection' request wrapper object for " + entityType.getName() + ".  Use this object in a query to request things like pagination or aggregation in an argument.  Use the 'content' field to request actual fields ")
                .type(pageType)
                .dataFetcher(new ExtendedJpaDataFetcher(entityManager, entityType))
                .argument(paginationArgument)
                .build();
    }


    private static final GraphQLArgument paginationArgument =
            GraphQLArgument.newArgument()
                    .name(PAGINATION_REQUEST_PARAM_NAME)
                    .type(GraphQLInputObjectType.newInputObject()
                                    .name("PaginationObject")
                                    .description("Query object for Pagination Requests, specifying the requested page, and that page's size.\n\nNOTE: 'page' parameter is 1-indexed, NOT 0-indexed.\n\nExample: paginationRequest { page: 1, size: 20 }")
                                    .field(GraphQLInputObjectField.newInputObjectField().name("page").description("Which page should be returned, starting with 1 (1-indexed)").type(Scalars.GraphQLInt).build())
                                    .field(GraphQLInputObjectField.newInputObjectField().name("size").description("How many results should this page contain").type(Scalars.GraphQLInt).build())
                                    .build()
                    ).build();




}
