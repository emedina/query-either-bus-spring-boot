package com.emedina.query.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;

import com.emedina.sharedkernel.query.Query;
import com.emedina.sharedkernel.query.core.QueryHandler;

/**
 * A registry that holds the mapping between a query and its handler using Spring's {@link ApplicationContext}.
 *
 * @author Enrique Medina Montenegro
 * @see QueryHandler
 */
public final class Registry {

    private final Map<Class<? extends Query>, QueryProvider<?>> providerMap = new HashMap<>();

    /**
     * Constructor-based dependency injection.
     *
     * @param applicationContext Spring's application context
     */
    public Registry(final ApplicationContext applicationContext) {
        String[] names = applicationContext.getBeanNamesForType(QueryHandler.class);
        for (String name : names) {
            this.register(applicationContext, name);
        }
    }

    /**
     * Looks up the name of the Bean (as a {@link QueryHandler}) in Spring's application context.
     *
     * @param applicationContext Spring's application context
     * @param name               of the bean as a query handler
     */
    @SuppressWarnings("unchecked")
    private void register(final ApplicationContext applicationContext, final String name) {
        Class<QueryHandler<?, ?, ?>> handlerClass = (Class<QueryHandler<?, ?, ?>>) applicationContext.getType(name);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handlerClass, QueryHandler.class);

        if (generics == null || generics.length < 3) {
            throw new IllegalStateException("Could not resolve query type for handler: " + name);
        }

        Class<? extends Query> queryType = (Class<? extends Query>) generics[2];
        this.providerMap.put(queryType, new QueryProvider<>(applicationContext, handlerClass));
    }

    /**
     * Retrieves the query handler for the given query class.
     *
     * @param queryClass the class of the query
     * @param <E>        the type of the error
     * @param <R>        the type of the result
     * @param <Q>        the type of the query
     * @return the query handler
     * @throws IllegalArgumentException if no handler is registered for the query class
     */
    @SuppressWarnings("unchecked")
    <E, R, Q extends Query> QueryHandler<E, R, Q> get(final Class<Q> queryClass) {
        QueryProvider<?> provider = this.providerMap.get(queryClass);
        if (provider == null) {
            throw new IllegalArgumentException("No query handler registered for: " + queryClass.getName());
        }

        return (QueryHandler<E, R, Q>) provider.get();
    }

}
