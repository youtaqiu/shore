package run.vexa.reactor.r2dbc.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveUpdateOperation;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.r2dbc.entity.BaseDomain;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DbUtils 测试类
 *
 * @author youta
 **/
class DbUtilsTest {

    private R2dbcEntityTemplate template;
    private BaseDomain<Long> testEntity;
    private ReactiveUpdateOperation.ReactiveUpdate reactiveUpdate;
    private ReactiveUpdateOperation.TerminatingUpdate terminatingUpdate;

    @BeforeEach
    void setUp() {
        template = mock(R2dbcEntityTemplate.class);
        reactiveUpdate = mock(ReactiveUpdateOperation.ReactiveUpdate.class);
        terminatingUpdate = mock(ReactiveUpdateOperation.TerminatingUpdate.class);

        // Set up the common mock behavior
        when(template.update(BaseDomain.class)).thenReturn(reactiveUpdate);
        when(reactiveUpdate.matching(any(Query.class))).thenReturn(terminatingUpdate);
        when(terminatingUpdate.apply(any(Update.class))).thenReturn(Mono.just(1L));

        testEntity = BaseDomain.<Long>builder()
                .id(1L)
                .createBy("Test Creator")
                .updateBy("Test Updater")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @Test
    void testUpdateSuccess() {
        // Arrange
        when(template.update(BaseDomain.class)).thenReturn(reactiveUpdate);
        when(reactiveUpdate.matching(any(Query.class))).thenReturn(terminatingUpdate);
        when(terminatingUpdate.apply(any(Update.class))).thenReturn(Mono.just(1L));

        // Act
        Mono<Long> result = DbUtils.update(template, testEntity);

        // Assert
        StepVerifier.create(result)
                .expectNext(1L)
                .verifyComplete();

        verify(template).update(BaseDomain.class);
        verify(reactiveUpdate).matching(any(Query.class));
        verify(terminatingUpdate).apply(any(Update.class));
    }

    @Test
    void testUpdateNoRowsAffected() {
        // Arrange
        when(terminatingUpdate.apply(any(Update.class))).thenReturn(Mono.just(0L));

        // Act
        Mono<Long> result = DbUtils.update(template, testEntity);

        // Assert
        StepVerifier.create(result)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void testUpdateWithNullId() {
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
    void testUpdateWithAllNullFields() {
        // Arrange
        BaseDomain<Long> emptyEntity = BaseDomain.<Long>builder()
                .id(1L)
                .build();

        // Act
        Mono<Long> result = DbUtils.update(template, emptyEntity);

        // Assert
        StepVerifier.create(result)
                .expectNext(1L)  // Expecting success with 1 row updated
                .verifyComplete();

        // Verify the interactions
        verify(template).update(BaseDomain.class);
        verify(reactiveUpdate).matching(any(Query.class));
        verify(terminatingUpdate).apply(any(Update.class));
    }

    @Test
    void testUpdateWithExceptionDuringPropertyAccess() {
        // Arrange
        BaseDomain<Long> spyEntity = spy(new BaseDomain<>());
        spyEntity.setId(1L);
        doThrow(new RuntimeException("Access error")).when(spyEntity).getCreateBy();

        // Act
        Mono<Long> result = DbUtils.update(template, spyEntity);

        // Assert
        StepVerifier.create(result)
                .expectError(ServerException.class)
                .verify();
    }

    @Test
    void testUpdateWithDatabaseError() {
        // Arrange
        when(terminatingUpdate.apply(any(Update.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Long> result = DbUtils.update(template, testEntity);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testUpdateWithIgnoredProperties() {
        // 确保 'class' 属性被忽略
        Mono<Long> result = DbUtils.update(template, testEntity);

        StepVerifier.create(result)
                .expectNextMatches(count -> count == 1L)
                .verifyComplete();

        // 验证更新时不包含 'class' 属性
        verify(terminatingUpdate, never()).apply(argThat(update ->
                update.toString().contains("class")));
    }
}

