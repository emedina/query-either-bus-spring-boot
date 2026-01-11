package com.emedina.query.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import com.emedina.query.spring.fixtures.TestQueryHandler;

/**
 * Unit tests for QueryProvider.
 *
 * @author Enrique Medina Montenegro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QueryProvider")
class QueryProviderTest {

    @Mock
    private ApplicationContext applicationContext;

    private QueryProvider<TestQueryHandler> queryProvider;

    @BeforeEach
    void setUp() {
        queryProvider = new QueryProvider<>(applicationContext, TestQueryHandler.class);
    }

    @Test
    @DisplayName("should get bean from application context")
    void shouldGetBeanFromApplicationContext() {
        // given
        TestQueryHandler expectedHandler = new TestQueryHandler();
        when(applicationContext.getBean(TestQueryHandler.class)).thenReturn(expectedHandler);

        // when
        TestQueryHandler actualHandler = queryProvider.get();

        // then
        verify(applicationContext).getBean(TestQueryHandler.class);
        assertThat(actualHandler).isSameAs(expectedHandler);
    }

    @Test
    @DisplayName("should delegate to application context getBean method")
    void shouldDelegateToApplicationContextGetBeanMethod() {
        // given
        TestQueryHandler handler = new TestQueryHandler();
        when(applicationContext.getBean(TestQueryHandler.class)).thenReturn(handler);

        // when
        queryProvider.get();

        // then
        verify(applicationContext).getBean(TestQueryHandler.class);
    }

    @Test
    @DisplayName("should return same instance as application context")
    void shouldReturnSameInstanceAsApplicationContext() {
        // given
        TestQueryHandler handler = new TestQueryHandler();
        when(applicationContext.getBean(TestQueryHandler.class)).thenReturn(handler);

        // when
        TestQueryHandler result1 = queryProvider.get();
        TestQueryHandler result2 = queryProvider.get();

        // then
        assertThat(result1).isSameAs(handler);
        assertThat(result2).isSameAs(handler);
        assertThat(result1).isSameAs(result2);
    }

    @Test
    @DisplayName("should handle null return from application context")
    void shouldHandleNullReturnFromApplicationContext() {
        // given
        when(applicationContext.getBean(TestQueryHandler.class)).thenReturn(null);

        // when
        TestQueryHandler result = queryProvider.get();

        // then
        assertThat(result).isNull();
    }

}
