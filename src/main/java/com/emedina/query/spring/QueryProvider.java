package com.emedina.query.spring;

import org.springframework.context.ApplicationContext;

import com.emedina.sharedkernel.query.core.QueryHandler;

/**
 * Creates a query handler that makes use of Spring's dependency injection capabilities.
 *
 * @param <H> type of the query handler
 * @author Enrique Medina Montenegro
 */
class QueryProvider<H extends QueryHandler<?, ?>> {

    private final ApplicationContext applicationContext;
    private final Class<H> type;

    /**
     * Constructor-based dependency injection.
     *
     * @param applicationContext Spring's application context
     * @param type               of the query handler
     */
    QueryProvider(final ApplicationContext applicationContext, final Class<H> type) {
        this.applicationContext = applicationContext;
        this.type = type;
    }

    public H get() {
        return this.applicationContext.getBean(this.type);
    }

}
