package org.victorprocure.graphql.strategy;

import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.strategy.query.FilteredResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.PublicResolverBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by victo on 7/2/2017.
 */
public class JpaResolverBuilder extends FilteredResolverBuilder {
    private static Logger log = LoggerFactory.getLogger(JpaResolverBuilder.class);

    @Override
    public Collection<Resolver> buildQueryResolvers(Object querySourceBean, AnnotatedType beanType) {
        log.debug("querySource: " + querySourceBean + " annotated: " + beanType);
        return null;
    }

    @Override
    public Collection<Resolver> buildMutationResolvers(Object querySourceBean, AnnotatedType beanType) {
        log.debug("querySource: " + querySourceBean + " annotated: " + beanType);
        return null;
    }
}
