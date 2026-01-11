package com.emedina.query.spring.fixtures;

import com.emedina.sharedkernel.query.core.QueryHandler;

import io.vavr.control.Either;

/**
 * Another test query handler fixture for testing multiple query types.
 *
 * @author Enrique Medina Montenegro
 */
public class AnotherTestQueryHandler implements QueryHandler<Integer, AnotherTestQuery> {

    private boolean wasExecuted = false;
    private AnotherTestQuery lastQuery;
    private Integer result;

    public AnotherTestQueryHandler() {
        this.result = 42;
    }

    public AnotherTestQueryHandler(Integer result) {
        this.result = result;
    }

    @Override
    public Either<?, Integer> handle(AnotherTestQuery query) {
        this.wasExecuted = true;
        this.lastQuery = query;
        return Either.right(result);
    }

    public boolean wasExecuted() {
        return wasExecuted;
    }

    public AnotherTestQuery getLastQuery() {
        return lastQuery;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public void reset() {
        this.wasExecuted = false;
        this.lastQuery = null;
    }

}
