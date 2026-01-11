package com.emedina.query.spring.fixtures;

import com.emedina.sharedkernel.query.Query;

/**
 * Test query fixture for testing the query bus.
 *
 * @author Enrique Medina Montenegro
 */
public class TestQuery implements Query {

    private final String message;

    public TestQuery(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TestQuery that = (TestQuery) obj;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TestQuery{message='" + message + "'}";
    }

}
