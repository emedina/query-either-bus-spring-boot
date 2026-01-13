# 🔍 Spring Query Either Bus

![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Java Version](https://img.shields.io/badge/Java-25-blue)
![Test Coverage](https://img.shields.io/badge/coverage-90%25-brightgreen)

A lightweight query bus implementation for Spring Boot applications that enables centralized query handling using Spring's dependency injection capabilities and functional programming with Either types.

## 📚 Further Learning

This implementation is part of a comprehensive exploration of Hexagonal Architecture patterns. The concepts are covered in depth in:

**English Version**
*Decoupling by Design: A Pragmatic Approach to Hexagonal Architecture*

- [PDF](https://leanpub.com/decouplingbydesignapractitionersguidetohexagonalarchitecture)  
- [Kindle](https://a.co/d/4KwauyK)  
- [Paperback](https://a.co/d/cGQI8gX)  

**Versión en Español**  
*Desacoplamiento por Diseño: Una Guía Práctica para la Arquitectura Hexagonal*

- [PDF](https://leanpub.com/desacoplamientopordiseounaguaprcticaparalaarquitecturahexagonal)  
- [Kindle](https://amzn.eu/d/ic50CoH)  
- [Tapa blanda](https://amzn.eu/d/1fHOpN6)  

The book provides in-depth coverage of:

- Functional query handling with Either types
- Spring Boot integration for functional error handling
- Vavr Either pattern implementations
- Composable error handling strategies
- Query pattern variations with monadic results
- Hexagonal architecture with functional programming
- Real-world applications of Either in query buses
- Testing strategies for functional query handlers

## 🎯 Overview

This library provides a clean implementation of the Query pattern integrated with Spring's application context and functional error handling using Vavr's Either type. It automatically discovers query handlers and routes queries to their appropriate handlers, promoting loose coupling and separation of concerns in your application architecture.

## ✨ Features

- **🔍 Automatic Handler Discovery**: Automatically registers query handlers from Spring's application context
- **🎯 Type-Safe Query Routing**: Routes queries to their corresponding handlers based on generic type resolution
- **🔧 Spring Integration**: Leverages Spring's dependency injection for handler instantiation
- **⚡ Lightweight**: Minimal overhead with clean, focused API
- **🏗️ Hexagonal Architecture Support**: Perfect for implementing the query side of CQRS patterns
- **🛡️ Functional Error Handling**: Uses Vavr's Either type for robust error handling without exceptions
- **🔄 Either Monad Support**: Enables functional composition and error propagation
- **📊 Typed Return Values**: Handlers return typed results wrapped in Either for safe error handling

## 📦 Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.emedina</groupId>
    <artifactId>query-either-bus-spring-boot</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🚀 Quick Start

### 1️⃣ Create a Query

```java
import com.emedina.sharedkernel.query.Query;

public class FindUserByIdQuery implements Query {
    private final Long userId;
    
    public FindUserByIdQuery(Long userId) {
        this.userId = userId;
    }
    
    public Long getUserId() {
        return userId;
    }
}
```

### 2️⃣ Create a Query Handler

```java
import com.emedina.sharedkernel.query.core.QueryHandler;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;

@Component
public class FindUserByIdQueryHandler implements QueryHandler<UserDto, FindUserByIdQuery> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Either<String, UserDto> handle(FindUserByIdQuery query) {
        try {
            User user = userRepository.findById(query.getUserId())
                .orElse(null);
            
            if (user == null) {
                return Either.left("User not found with id: " + query.getUserId());
            }
            
            UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getEmail());
            return Either.right(dto);
        } catch (Exception e) {
            return Either.left("Failed to fetch user: " + e.getMessage());
        }
    }
}
```

### 3️⃣ Configure the Query Bus

```java
import com.emedina.query.spring.Registry;
import com.emedina.query.spring.SpringQueryBus;
import com.emedina.sharedkernel.query.core.QueryBus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryBusConfiguration {
    
    @Bean
    public Registry registry(ApplicationContext applicationContext) {
        return new Registry(applicationContext);
    }
    
    @Bean
    public QueryBus queryBus(Registry registry) {
        return new SpringQueryBus(registry);
    }
}
```

### 4️⃣ Use the Query Bus

```java
import com.emedina.sharedkernel.query.core.QueryBus;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private QueryBus queryBus;
    
    public Either<String, UserDto> getUserById(Long userId) {
        FindUserByIdQuery query = new FindUserByIdQuery(userId);
        return queryBus.query(query);
    }
}
```

## 🏗️ Architecture

The query bus consists of three main components:

### 🔍 QueryBus

The main interface for executing queries. The `SpringQueryBus` implementation routes queries to their handlers and returns Either types for functional error handling.

### 📋 Registry

Maintains the mapping between query types and their handlers. It automatically discovers handlers from Spring's application context using generic type resolution.

### 🏭 QueryProvider

A factory that creates query handler instances using Spring's dependency injection capabilities.

## ⚙️ How It Works

1. **🔍 Handler Discovery**: On startup, the `Registry` scans the Spring application context for beans implementing `QueryHandler<R, Q>`
2. **🧬 Type Resolution**: Uses Spring's `GenericTypeResolver` to determine which query type each handler processes
3. **📝 Handler Registration**: Maps query types to their corresponding handler providers
4. **🚀 Query Execution**: When a query is executed, the bus looks up the appropriate handler and delegates execution
5. **🛡️ Error Handling**: Returns Either<Error, Result> for functional error handling without exceptions

## 🔄 Either Type Benefits

The Either type provides several advantages:

- **🚫 No Exceptions**: Avoid exception-based error handling
- **🔗 Composable**: Chain operations functionally
- **🎯 Explicit**: Make error cases explicit in the type system
- **🛡️ Safe**: Compile-time safety for error handling
- **📊 Typed Results**: Return typed results safely

### Example with Error Handling

```java
public Either<QueryError, UserDto> getUserWithFallback(Long userId) {
    return queryBus.query(new FindUserByIdQuery(userId))
        .mapLeft(error -> new QueryError("Query failed", error))
        .peek(user -> log.info("User retrieved: {}", user.getUsername()))
        .orElse(() -> Either.right(getDefaultUser()));
}
```

### Composing Multiple Queries

```java
public Either<String, UserProfileDto> getUserProfile(Long userId) {
    return queryBus.query(new FindUserByIdQuery(userId))
        .flatMap(user -> queryBus.query(new FindUserPreferencesQuery(user.getId()))
            .map(prefs -> new UserProfileDto(user, prefs)));
}
```

## 🧪 Testing

The library includes comprehensive unit and integration tests. Run tests with:

```bash
mvn test
```

### 📊 Test Coverage

- ✅ **Unit Tests**: All components tested with Mockito
- ✅ **Integration Tests**: Real Spring context validation
- ✅ **Edge Cases**: Missing handlers, null results, and empty contexts covered
- ✅ **Either Handling**: Both success and error paths tested
- ✅ **90%+ Coverage**: Comprehensive test suite with JaCoCo

### 🔧 JaCoCo Coverage

Generate coverage reports:

```bash
mvn clean test jacoco:report
```

View the coverage report at `target/site/jacoco/index.html`

## 📋 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| **Spring Framework** | 7.0.2 | Core Spring integration |
| **Java** | 25 | Runtime platform |
| **Vavr** | 0.11.0 | Functional programming with Either |
| **Shared Kernel Query Either Bus** | 1.0.0 | Query interfaces |

### Test Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| **JUnit Jupiter** | 6.0.2 | Testing framework |
| **Mockito** | 5.21.0 | Mocking framework |
| **AssertJ** | 3.27.6 | Fluent assertions |

## 🤝 Contributing

1. 🍴 Fork the repository
2. 🌿 Create a feature branch
3. ✅ Add tests for your changes
4. 🧪 Ensure all tests pass
5. 📊 Maintain 90%+ test coverage
6. 📤 Submit a pull request

## 📄 License

This project is part of the hexagonal architecture examples and follows the same licensing terms.

## 👨‍💻 Author

**Enrique Medina Montenegro**

---

## 🏷️ Tags

`spring-boot` `query-either-bus` `cqrs` `hexagonal-architecture` `ddd` `query-pattern` `spring-framework` `dependency-injection` `either` `functional-programming` `vavr` `error-handling`

---

*🎯 This library is designed to support clean architecture principles and CQRS patterns in Spring Boot applications with functional error handling using Either types.*
