package com.emedina.query.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import com.emedina.query.spring.fixtures.AnotherTestQuery;
import com.emedina.query.spring.fixtures.AnotherTestQueryHandler;
import com.emedina.query.spring.fixtures.RawTypeQueryHandler;
import com.emedina.query.spring.fixtures.TestQuery;
import com.emedina.query.spring.fixtures.TestQueryHandler;
import com.emedina.sharedkernel.query.core.QueryHandler;

import io.vavr.control.Either;

/**
 * Unit tests for Registry.
 *
 * @author Enrique Medina Montenegro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Registry")
class RegistryTest {

    @Mock
    private ApplicationContext applicationContext;

    private Registry registry;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setupWithHandlers() {
        when(applicationContext.getBeanNamesForType(QueryHandler.class))
            .thenReturn(new String[] { "testQueryHandler", "anotherTestQueryHandler" });

        when(applicationContext.getType("testQueryHandler"))
            .thenReturn((Class) TestQueryHandler.class);
        when(applicationContext.getType("anotherTestQueryHandler"))
            .thenReturn((Class) AnotherTestQueryHandler.class);

        when(applicationContext.getBean(TestQueryHandler.class))
            .thenReturn(new TestQueryHandler());
        when(applicationContext.getBean(AnotherTestQueryHandler.class))
            .thenReturn(new AnotherTestQueryHandler());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setupWithTestHandlerOnly() {
        when(applicationContext.getBeanNamesForType(QueryHandler.class))
            .thenReturn(new String[] { "testQueryHandler" });

        when(applicationContext.getType("testQueryHandler"))
            .thenReturn((Class) TestQueryHandler.class);

        when(applicationContext.getBean(TestQueryHandler.class))
            .thenReturn(new TestQueryHandler());
    }

    private void setupWithoutHandlers() {
        when(applicationContext.getBeanNamesForType(QueryHandler.class))
            .thenReturn(new String[] {});
    }

    @Test
    @DisplayName("should register query handlers during construction")
    void shouldRegisterQueryHandlersDuringConstruction() {
        // given
        setupWithHandlers();

        // when
        registry = new Registry(applicationContext);

        // then
        QueryHandler<Throwable, String, TestQuery> testHandler = registry.get(TestQuery.class);
        QueryHandler<Throwable, Integer, AnotherTestQuery> anotherHandler = registry.get(AnotherTestQuery.class);

        assertThat(testHandler).isNotNull();
        assertThat(testHandler).isInstanceOf(TestQueryHandler.class);
        assertThat(anotherHandler).isNotNull();
        assertThat(anotherHandler).isInstanceOf(AnotherTestQueryHandler.class);
    }

    @Test
    @DisplayName("should return correct handler for query type")
    void shouldReturnCorrectHandlerForQueryType() {
        // given
        setupWithTestHandlerOnly();
        registry = new Registry(applicationContext);

        // when
        QueryHandler<Throwable, String, TestQuery> handler = registry.get(TestQuery.class);

        // then
        assertThat(handler).isInstanceOf(TestQueryHandler.class);

        TestQuery query = new TestQuery("test");
        Either<?, String> result = handler.handle(query);

        TestQueryHandler testHandler = (TestQueryHandler) handler;
        assertThat(testHandler.wasExecuted()).isTrue();
        assertThat(testHandler.getLastQuery()).isEqualTo(query);
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("default result");
    }

    @Test
    @DisplayName("should handle multiple query types")
    void shouldHandleMultipleQueryTypes() {
        // given
        setupWithHandlers();
        registry = new Registry(applicationContext);

        // when
        QueryHandler<Throwable, String, TestQuery> testHandler = registry.get(TestQuery.class);
        QueryHandler<Throwable, Integer, AnotherTestQuery> anotherHandler = registry.get(AnotherTestQuery.class);

        // then
        assertThat(testHandler).isInstanceOf(TestQueryHandler.class);
        assertThat(anotherHandler).isInstanceOf(AnotherTestQueryHandler.class);
        assertThat(testHandler).isNotSameAs(anotherHandler);
    }

    @Test
    @DisplayName("should return null when no handler registered for query type")
    void shouldReturnNullWhenNoHandlerRegisteredForQueryType() {
        // given
        setupWithoutHandlers();
        registry = new Registry(applicationContext);

        // when & then
        assertThatThrownBy(() -> registry.get(TestQuery.class))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should handle empty application context")
    void shouldHandleEmptyApplicationContext() {
        // given
        setupWithoutHandlers();

        // when
        registry = new Registry(applicationContext);

        // then
        assertThatThrownBy(() -> registry.get(TestQuery.class))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> registry.get(AnotherTestQuery.class))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should throw IllegalStateException when handler has no generic type information")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void shouldThrowIllegalStateExceptionWhenHandlerHasNoGenericTypeInformation() {
        // given
        when(applicationContext.getBeanNamesForType(QueryHandler.class))
            .thenReturn(new String[] { "rawTypeQueryHandler" });
        when(applicationContext.getType("rawTypeQueryHandler"))
            .thenReturn((Class) RawTypeQueryHandler.class);

        // when & then
        assertThatThrownBy(() -> new Registry(applicationContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Could not resolve query type for handler: rawTypeQueryHandler");
    }

    @Test
    @DisplayName("should throw IllegalStateException when generic type resolution returns null")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void shouldThrowIllegalStateExceptionWhenGenericTypeResolutionReturnsNull() {
        // given
        when(applicationContext.getBeanNamesForType(QueryHandler.class))
            .thenReturn(new String[] { "rawTypeQueryHandler" });
        when(applicationContext.getType("rawTypeQueryHandler"))
            .thenReturn((Class) RawTypeQueryHandler.class);

        // when & then
        assertThatThrownBy(() -> new Registry(applicationContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Could not resolve query type for handler: rawTypeQueryHandler");
    }

}
