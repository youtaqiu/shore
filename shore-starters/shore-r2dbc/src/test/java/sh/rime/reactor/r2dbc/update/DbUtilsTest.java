package sh.rime.reactor.r2dbc.update;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveUpdateOperation;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.r2dbc.entity.BaseDomain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DbUtils unit test.
 *
 * @author rained
 **/
class DbUtilsTest {

    private R2dbcEntityTemplate template;
    private BaseDomain<Long> testEntity;

    @BeforeEach
    void setUp() {
        template = mock(R2dbcEntityTemplate.class);
        testEntity = new BaseDomain<>();
        testEntity.setId(1L);
        testEntity.setCreateBy("Test Name");
        testEntity.setUpdateBy("25");
    }

    @Test
    void testUpdateSuccess() {
        // Arrange
        ReactiveUpdateOperation.ReactiveUpdate reactiveUpdate = mock(ReactiveUpdateOperation.ReactiveUpdate.class);
        ReactiveUpdateOperation.TerminatingUpdate terminatingUpdate = mock(ReactiveUpdateOperation.TerminatingUpdate.class);

        when(template.update(eq(BaseDomain.class))).thenReturn(reactiveUpdate);
        when(reactiveUpdate.matching(any(Query.class))).thenReturn(terminatingUpdate);
        when(terminatingUpdate.apply(any(Update.class))).thenReturn(Mono.just(1L));

        // Act
        Mono<Long> result = DbUtils.update(template, testEntity);

        // Assert
        StepVerifier.create(result)
                .expectNext(1L)
                .verifyComplete();

        verify(template, times(1)).update(eq(BaseDomain.class));
        verify(reactiveUpdate, times(1)).matching(any(Query.class));
        verify(terminatingUpdate, times(1)).apply(any(Update.class));
    }

    @Test
    void updateNoIdFieldShouldReturnError() {
        // Arrange
        testEntity.setId(null);

        // Act
        Mono<Long> result = DbUtils.update(template, testEntity);

        // Assert
        StepVerifier.create(result)
                .expectError(ServerException.class)
                .verify();
    }


    @Test
    void updateErrorDuringPropertyAccessShouldReturnError() {
        // Arrange
        BaseDomain<Long> entity = spy(new BaseDomain<>());

        // 使用 RuntimeException 而不是 IllegalAccessException
        doThrow(new RuntimeException("Access error")).when(entity).getId();

        // Act
        Mono<Long> result = DbUtils.update(template, entity);

        // Assert
        StepVerifier.create(result)
                .expectError(ServerException.class)
                .verify();
    }

}

