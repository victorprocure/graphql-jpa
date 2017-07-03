package org.victorprocure.graphql.mutations;

import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victorprocure.graphql.annotation.GraphQLDoNotChange;
import org.victorprocure.graphql.utils.FieldUtils;
import org.victorprocure.graphql.utils.SchemaUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Created by victo on 7/1/2017.
 */
public class MutationProvider implements IMutationProvider {

    private static final Logger log = LoggerFactory.getLogger(MutationProvider.class);

    private GraphQLSchema.Builder schemaBuilder;
    private EntityManager entityManager;

    public void buildMutation() {}

    public void addMutation(GraphQLSchema.Builder schemaBuilder, EntityManager entityManager) {
        this.schemaBuilder = schemaBuilder;
        this.entityManager = entityManager;

        GraphQLObjectType mutation = newObject().name("Mutation")
                .fields(entityManager.getMetamodel().getEntities().stream().filter(SchemaUtils::isNotIgnored)
                        .map(this::createMutation).collect(Collectors.toList()))
                .build();

        schemaBuilder.mutation(mutation);
    }

    private GraphQLFieldDefinition createMutation(EntityType<?> entityType) {
        String name = entityType.getName();

        return newFieldDefinition().name(name)
             .description(SchemaUtils.getSchemaDocumentation(entityType.getJavaType()))
            .type(new GraphQLTypeReference(name))
            .argument(entityType.getAttributes().stream().filter(SchemaUtils::isValidInput)
						.filter(SchemaUtils::isNotIgnored).map(SchemaUtils::getArgument)
						.collect(Collectors.toList()))
            .dataFetcher(environment -> {
        try {
            return updateEntity(environment);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }).build();
}

    private Object updateEntity(DataFetchingEnvironment env)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        EntityType et = entityManager.getMetamodel().getEntities().stream().filter(it->it.getName().equals(env.getFieldType().getName())).findFirst().get();
        Class<?> clazz = et.getJavaType();
        Object newInstance = clazz.newInstance();
        SingularAttribute<?, ?> pkattr = et.getId(et.getIdType().getJavaType());
        String primaryKeyName = pkattr.getName();
        Object primaryKey = env.getArgument(primaryKeyName);

        log.debug(
                new StringBuilder()
                        .append("Instance: ").append(newInstance.getClass().getName())
                        .append(", Primary Key Name: ").append(primaryKeyName)
                        .append(" Value: ").append(primaryKey).toString()
        );

        boolean isNew = primaryKey == null;

        if(!isNew) {
            Object found = entityManager.find(clazz, primaryKey);
            if (found != null) {
                newInstance = found;
            } else {
                isNew = true;
            }
        }

        Object instance = newInstance;
        boolean finalIsNew = isNew;

        env.getArguments().entrySet().stream()
                .filter(a -> a.getValue() != null)
                .filter(a-> this.isValidArgument(instance, finalIsNew, a))
                .forEach(a -> FieldUtils.setFieldValueByFieldName(instance, a.getKey(), a.getValue()));

        return entityManager.merge(instance);
    }

    private boolean isValidArgument(Object instance, boolean isNew, Map.Entry<String, Object> entry) {
        Field field = FieldUtils.getFieldByName(instance, entry.getKey());

        return !FieldUtils.fieldHasAnnotation(GraphQLDoNotChange.class, field);
    }
}
