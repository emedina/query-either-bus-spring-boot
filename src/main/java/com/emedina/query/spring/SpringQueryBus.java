package com.emedina.query.spring;

import org.springframework.context.annotation.Bean;

import com.emedina.sharedkernel.query.Query;
import com.emedina.sharedkernel.query.core.QueryBus;
import com.emedina.sharedkernel.query.core.QueryHandler;

import io.vavr.control.Either;

/**
 * Implementation of a query bus backed by Spring's registry.
 *
 * @author Enrique Medina Montenegro
 */
public class SpringQueryBus implements QueryBus {

    private final Registry registry;

    /**
     * Creates a new instance with the given registry using constructor-based dependency injection.
     *
     * @param registry a wrapper around Spring's application context
     */
    public SpringQueryBus(final Registry registry) {
        this.registry = registry;
    }

    /**
     * Delegates the handling of the query to the corresponding {@link Bean} from Spring.
     *
     * @param query the query object
     * @param <R>   the type of the result
     * @param <Q>   the type of the query
     * @return either success with result, or an error if anything goes wrong
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R, Q extends Query> Either<?, R> query(final Q query) {
        Class<Q> queryClass = (Class<Q>) query.getClass();
        QueryHandler<R, Q> queryHandler = this.registry.get(queryClass);
        return queryHandler.handle(query);
    }

}
