package com.emedina.query.spring.fixtures;

import com.emedina.sharedkernel.query.core.QueryHandler;

import io.vavr.control.Either;

/**
 * Test query handler fixture for testing the query bus.
 *
 * @author Enrique Medina Montenegro
 */
public class TestQueryHandler implements QueryHandler<String, TestQuery> {

    private boolean wasExecuted = false;
    private TestQuery lastQuery;
    private String result;

    public TestQueryHandler() {
        this.result = "default result";
    }

    public TestQueryHandler(String result) {
        this.result = result;
    }

    @Override
    public Either<?, String> handle(TestQuery query) {
        this.wasExecuted = true;
        this.lastQuery = query;
        return Either.right(result);
    }

    public boolean wasExecuted() {
        return wasExecuted;
    }

    public TestQuery getLastQuery() {
        return lastQuery;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void reset() {
        this.wasExecuted = false;
        this.lastQuery = null;
    }

}
