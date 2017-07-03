package org.victorprocure.graphql.strategy;

import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.strategy.query.AnnotatedArgumentBuilder;
import io.leangen.graphql.metadata.strategy.query.BeanOperationNameGenerator;
import io.leangen.graphql.metadata.strategy.query.FilteredResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.PublicResolverBuilder;
import io.leangen.graphql.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victorprocure.graphql.configuration.IGraphQLJpaConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by victo on 7/2/2017.
 */
public class JpaResolverBuilder extends FilteredResolverBuilder {
    private static Logger log = LoggerFactory.getLogger(JpaResolverBuilder.class);
    private EntityManager entityManager;

    public JpaResolverBuilder(EntityManager entityManager) {
        this.operationNameGenerator = new BeanOperationNameGenerator();
        this.argumentExtractor = new AnnotatedArgumentBuilder();
        this.entityManager = entityManager;
    }

    @Override
    public Collection<Resolver> buildQueryResolvers(Object querySourceBean, AnnotatedType beanType) {

        return this.buildQueryResolvers(querySourceBean, beanType, this.getFilters());
    }

    @Override
    public Collection<Resolver> buildMutationResolvers(Object querySourceBean, AnnotatedType beanType) {

        return this.buildMutationResolvers(querySourceBean, beanType, this.getFilters());
    }

    private Collection<Resolver> buildQueryResolvers(Object querySourceBean, AnnotatedType beanType, List<Predicate<Member>> filters) {
        log.debug(
                new StringBuilder()
                .append("querySource: ")
                        .append(querySourceBean)
                        .append(" annotated: ")
                        .append(beanType)
                        .append(" filters: ")
                        .append(filters)
                        .toString());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Type t = beanType.getType();
        Class<?> clazz = t.getClass();
        CriteriaQuery<Object> query = cb.createQuery((Class) t);
        Root root = query.from(clazz);

    return Collections.emptyList();
    }

    private Collection<Resolver> buildMutationResolvers(Object querySourceBean, AnnotatedType beanType, List<Predicate<Member>> filters) {
        log.debug(
                new StringBuilder()
                        .append("querySource: ")
                        .append(querySourceBean)
                        .append(" annotated: ")
                        .append(beanType)
                        .append(" filters: ")
                        .append(filters)
                        .toString());
    return Collections.emptyList();
    }
}
