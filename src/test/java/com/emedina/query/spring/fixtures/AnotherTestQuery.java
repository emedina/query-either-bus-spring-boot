package com.emedina.query.spring.fixtures;

import com.emedina.sharedkernel.query.Query;

/**
 * Another test query fixture for testing multiple query types.
 *
 * @author Enrique Medina Montenegro
 */
public class AnotherTestQuery implements Query {

    private final int value;

    public AnotherTestQuery(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AnotherTestQuery that = (AnotherTestQuery) obj;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return "AnotherTestQuery{value=" + value + "}";
    }

}
