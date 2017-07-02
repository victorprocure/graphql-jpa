package org.victorprocure.graphql.utils;

import graphql.Scalars;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victorprocure.graphql.IdentityCoercing;
import org.victorprocure.graphql.JavaScalars;
import org.victorprocure.graphql.annotation.GraphQLIgnore;
import org.victorprocure.graphql.annotation.SchemaDocumentation;

import javax.persistence.metamodel.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by victo on 7/1/2017.
 */
public final class SchemaUtils {
    private static Logger log = LoggerFactory.getLogger(SchemaUtils.class);

    public static GraphQLArgument getArgument(Attribute attribute) {
        GraphQLType type = getAttributeType(attribute);

        if (type instanceof GraphQLInputType) {
            return GraphQLArgument.newArgument()
                    .name(attribute.getName())
                    .type((GraphQLInputType) type)
                    .build();
        }

        throw new IllegalArgumentException("Attribute " + attribute + " cannot be mapped as an Input Argument");
    }

    public static GraphQLObjectType getObjectType(EntityType<?> entityType) {
        return GraphQLObjectType.newObject()
                .name(entityType.getName())
                .description(getSchemaDocumentation( entityType.getJavaType()))
                .fields(entityType.getAttributes().stream().filter(SchemaUtils::isNotIgnored).map(SchemaUtils::getObjectField).collect(Collectors.toList()))
                .build();
    }

    public static GraphQLFieldDefinition getObjectField(Attribute attribute) {
        GraphQLType type = getAttributeType(attribute);

        if (type instanceof GraphQLOutputType) {
            List<GraphQLArgument> arguments = new ArrayList<>();
            arguments.add(GraphQLArgument.newArgument().name("orderBy").type(orderByDirectionEnum).build());            // Always add the orderBy argument

            // Get the fields that can be queried on (i.e. Simple Types, no Sub-Objects)
            if (attribute instanceof SingularAttribute && attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
                Stream<Attribute> attributes = findBasicAttributes(foreignType.getAttributes());

                attributes.forEach(it -> {
                    arguments.add(GraphQLArgument.newArgument().name(it.getName()).type((GraphQLInputType) getAttributeType(it)).build());
                });
            }

            return GraphQLFieldDefinition.newFieldDefinition()
                    .name(attribute.getName())
                    .description(getSchemaDocumentation(attribute.getJavaMember()))
                    .type((GraphQLOutputType) type)
                    .argument(arguments)
                    .build();
        }

        throw new IllegalArgumentException("Attribute " + attribute + " cannot be mapped as an Output Argument");
    }

    public static Stream<Attribute> findBasicAttributes(Collection<Attribute> attributes) {
        return attributes.stream().filter(it -> it.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC);
    }

    public static GraphQLType getAttributeType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            if (String.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLString;
            else if (UUID.class.isAssignableFrom(attribute.getJavaType()))
                return JavaScalars.GraphQLUUID;
            else if (Integer.class.isAssignableFrom(attribute.getJavaType()) || int.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLInt;
            else if (Short.class.isAssignableFrom(attribute.getJavaType()) || short.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLShort;
            else if (Float.class.isAssignableFrom(attribute.getJavaType()) || float.class.isAssignableFrom(attribute.getJavaType())
                    || Double.class.isAssignableFrom(attribute.getJavaType()) || double.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLFloat;
            else if (Long.class.isAssignableFrom(attribute.getJavaType()) || long.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLLong;
            else if (Boolean.class.isAssignableFrom(attribute.getJavaType()) || boolean.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLBoolean;
            else if (Date.class.isAssignableFrom(attribute.getJavaType()))
                return JavaScalars.GraphQLDate;
            else if (LocalDateTime.class.isAssignableFrom(attribute.getJavaType()))
                return JavaScalars.GraphQLLocalDateTime;
            else if (Instant.class.isAssignableFrom(attribute.getJavaType()))
                return JavaScalars.GraphQLInstant;
            else if (LocalDate.class.isAssignableFrom(attribute.getJavaType()))
                return JavaScalars.GraphQLLocalDate;
            else if (attribute.getJavaType().isEnum()) {
                return getTypeFromJavaType(attribute.getJavaType());
            } else if (BigDecimal.class.isAssignableFrom(attribute.getJavaType())) {
                return Scalars.GraphQLBigDecimal;
            }
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName());
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            PluralAttribute pAttribute = ((PluralAttribute) attribute);
            Class tt = pAttribute.getJavaType();
            Type foreignType = pAttribute.getElementType();

            if(Map.class.isAssignableFrom(tt)){
                Type eType = pAttribute.getElementType();
                String name = eType.getJavaType().getName();
                return new GraphQLList(new GraphQLTypeReference(name));
            }else{

                return new GraphQLList(getTypeFromJavaType(foreignType.getJavaType()));
            }
        }

        final String declaringType = attribute.getDeclaringType().getJavaType().getName(); // fully qualified name of the entity class
        final String declaringMember = attribute.getJavaMember().getName(); // field name in the entity class

        throw new UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '" + declaringMember + "' of entity class '"+ declaringType +"'");
    }


    public static boolean isValidInput(Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC ||
                attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
    }

    public static String getSchemaDocumentation(Member member) {
        if (member instanceof AnnotatedElement) {
            return getSchemaDocumentation((AnnotatedElement) member);
        }

        return null;
    }

    public static String getSchemaDocumentation(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            SchemaDocumentation schemaDocumentation = annotatedElement.getAnnotation(SchemaDocumentation.class);
            return schemaDocumentation != null ? schemaDocumentation.value() : null;
        }

        return null;
    }

    public static boolean isNotIgnored(Attribute attribute) {
        return isNotIgnored(attribute.getJavaMember()) && isNotIgnored(attribute.getJavaType());
    }

    public static boolean isNotIgnored(EntityType entityType) {
        return isNotIgnored(entityType.getJavaType());
    }

    public static boolean isNotIgnored(Member member) {
        return member instanceof AnnotatedElement && isNotIgnored((AnnotatedElement) member);
    }

    public static boolean isNotIgnored(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            GraphQLIgnore schemaDocumentation = annotatedElement.getAnnotation(GraphQLIgnore.class);
            return schemaDocumentation == null;
        }

        return false;
    }

    public static GraphQLType getTypeFromJavaType(Class clazz) {
        if (clazz.isEnum()) {
            GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(clazz.getSimpleName());
            int ordinal = 0;
            for (Enum enumValue : ((Class<Enum>)clazz).getEnumConstants())
                enumBuilder.value(enumValue.name(), ordinal++);

            GraphQLType answer = enumBuilder.build();
            setIdentityCoercing(answer);

            return answer;
        }

        return null;
    }

    /**
     * A bit of a hack, since JPA will deserialize our Enum's for us...we don't want GraphQL doing it.
     *
     * @param type
     */
    public static void setIdentityCoercing(GraphQLType type) {
        try {
            Field coercing = type.getClass().getDeclaredField("coercing");
            coercing.setAccessible(true);
            coercing.set(type, new IdentityCoercing());
        } catch (Exception e) {
            log.error("Unable to set coercing for " + type, e);
        }
    }

    private static final GraphQLEnumType orderByDirectionEnum =
            GraphQLEnumType.newEnum()
                    .name("OrderByDirection")
                    .description("Describes the direction (Ascending / Descending) to sort a field.")
                    .value("ASC", 0, "Ascending")
                    .value("DESC", 1, "Descending")
                    .build();
}
