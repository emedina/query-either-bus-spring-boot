package com.emedina.query.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.emedina.query.spring.fixtures.TestQuery;
import com.emedina.query.spring.fixtures.TestQueryHandler;
import com.emedina.sharedkernel.query.core.QueryHandler;

import io.vavr.control.Either;

/**
 * Unit tests for SpringQueryBus.
 *
 * @author Enrique Medina Montenegro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SpringQueryBus")
class SpringQueryBusTest {

    @Mock
    private Registry registry;

    private SpringQueryBus queryBus;

    @BeforeEach
    void setUp() {
        queryBus = new SpringQueryBus(registry);
    }

    @Test
    @DisplayName("should execute query successfully when handler exists")
    @SuppressWarnings("unchecked")
    void shouldExecuteQuerySuccessfully() {
        // given
        TestQuery query = new TestQuery("test message");
        TestQueryHandler handler = new TestQueryHandler("test result");
        when(registry.get(TestQuery.class)).thenReturn((QueryHandler) handler);

        // when
        Either<?, String> result = queryBus.query(query);

        // then
        verify(registry).get(TestQuery.class);
        assertThat(handler.wasExecuted()).isTrue();
        assertThat(handler.getLastQuery()).isEqualTo(query);
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("test result");
    }

    @Test
    @DisplayName("should delegate to registry to get handler")
    @SuppressWarnings("unchecked")
    void shouldDelegateToRegistryToGetHandler() {
        // given
        TestQuery query = new TestQuery("test message");
        TestQueryHandler handler = new TestQueryHandler();
        when(registry.get(TestQuery.class)).thenReturn((QueryHandler) handler);

        // when
        queryBus.query(query);

        // then
        verify(registry).get(TestQuery.class);
    }

    @Test
    @DisplayName("should return Either from handler")
    @SuppressWarnings("unchecked")
    void shouldReturnEitherFromHandler() {
        // given
        TestQuery query = new TestQuery("test message");
        TestQueryHandler handler = new TestQueryHandler("expected result");
        when(registry.get(TestQuery.class)).thenReturn((QueryHandler) handler);

        // when
        Either<?, String> result = queryBus.query(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("expected result");
        assertThat(handler.wasExecuted()).isTrue();
        assertThat(handler.getLastQuery()).isEqualTo(query);
    }

    @Test
    @DisplayName("should handle null result from handler")
    @SuppressWarnings("unchecked")
    void shouldHandleNullResultFromHandler() {
        // given
        TestQuery query = new TestQuery("test message");
        TestQueryHandler handler = new TestQueryHandler(null);
        when(registry.get(TestQuery.class)).thenReturn((QueryHandler) handler);

        // when
        Either<?, String> result = queryBus.query(query);

        // then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isNull();
        assertThat(handler.wasExecuted()).isTrue();
    }

    @Test
    @DisplayName("should throw exception when registry returns null handler")
    void shouldThrowExceptionWhenRegistryReturnsNullHandler() {
        // given
        TestQuery query = new TestQuery("test message");
        when(registry.get(TestQuery.class)).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> queryBus.query(query))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should throw exception when query is null")
    void shouldThrowExceptionWhenQueryIsNull() {
        // given
        TestQuery nullQuery = null;

        // when/then
        assertThatThrownBy(() -> queryBus.query(nullQuery))
            .isInstanceOf(NullPointerException.class);
    }

}
