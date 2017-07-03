package org.victorprocure.graphql.strategy;

import graphql.execution.batched.Batched;
import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.execution.MethodInvoker;
import io.leangen.graphql.metadata.execution.SingletonMethodInvoker;
import io.leangen.graphql.metadata.strategy.query.*;
import io.leangen.graphql.util.ClassUtils;
import graphql.execution.batched.Batched;
import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.execution.MethodInvoker;
import io.leangen.graphql.metadata.execution.SingletonMethodInvoker;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.Utils;
import io.leangen.graphql.util.Utils;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by victo on 7/2/2017.
 */
public class JpaResolverBuilder extends PublicResolverBuilder {
    private static Logger log = LoggerFactory.getLogger(JpaResolverBuilder.class);
    private EntityManager entityManager;

    public JpaResolverBuilder(EntityManager entityManager) {
        super(null);
        this.operationNameGenerator = new MethodOperationNameGenerator();
        this.argumentExtractor = new AnnotatedArgumentBuilder();
        this.entityManager = entityManager;
    }


    @Override
    protected boolean isQuery(Method method) {
        return super.isQuery(method) && ClassUtils.isGetter(method);
    }

    @Override
    protected boolean isMutation(Method method) {
        return ClassUtils.isSetter(method);
    }

}
