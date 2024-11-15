package sh.rime.reactor.commoms.bean;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.commons.constants.CommonConstant;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * R unit test.
 * RTest
 */
class RTest {

    @Test
    void testOkMono() {
        Mono<String> monoBody = Mono.just("data");
        Mono<R<String>> response = R.ok(monoBody);

        StepVerifier.create(response)
                .expectNextMatches(r -> r.getCode().equals(CommonConstant.SUCCESS_CODE) && "data".equals(r.getData()))
                .verifyComplete();
    }

    @Test
    void testOkFlux() {
        Flux<String> fluxBody = Flux.just("data1", "data2");
        Mono<R<java.util.List<String>>> response = R.ok(fluxBody);

        StepVerifier.create(response)
                .expectNextMatches(r -> r.getCode().equals(CommonConstant.SUCCESS_CODE)
                        && r.getData().containsAll(Arrays.asList("data1", "data2")))
                .verifyComplete();
    }

    @Test
    void testFailedMono() {
        Mono<String> monoBody = Mono.just("data");
        Mono<R<String>> response = R.failed(monoBody);

        StepVerifier.create(response)
                .expectNextMatches(r -> r.getCode().equals(CommonConstant.ERROR_CODE) && "data".equals(r.getData()))
                .verifyComplete();
    }

    @Test
    void testError() {
        Mono<R<Object>> response = R.error(CommonExceptionEnum.BAD_REQUEST);
        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof ServerException
                        && CommonExceptionEnum.BAD_REQUEST.code() == ((ServerException) throwable).getErrorCode()
                        && CommonExceptionEnum.BAD_REQUEST.message().equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void okMonoWithMsg() {
        Mono<R<String>> result = R.ok(Mono.just("data"), "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.SUCCESS_CODE, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void okFluxWithMsg() {
        Mono<R<List<String>>> result = R.ok(Flux.just("data1", "data2"), "custom message");
        R<List<String>> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.SUCCESS_CODE, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals(List.of("data1", "data2"), r.getData());
    }

    @Test
    void okMonoWithCodeAndMsg() {
        Mono<R<String>> result = R.ok(Mono.just("data"), 1000, "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void okFluxWithCodeAndMsg() {
        Mono<R<List<String>>> result = R.ok(Flux.just("data1", "data2"), 1000, "custom message");
        R<List<String>> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals(List.of("data1", "data2"), r.getData());
    }

    @Test
    void okEmpty() {
        Mono<R<String>> result = R.ok();
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.SUCCESS_CODE, r.getCode());
        assertEquals(CommonConstant.SUCCESS_MSG, r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void okWithData() {
        Mono<R<String>> result = R.ok("data");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.SUCCESS_CODE, r.getCode());
        assertEquals(CommonConstant.SUCCESS_MSG, r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void okWithDataAndMsg() {
        Mono<R<String>> result = R.ok("data", "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.SUCCESS_CODE, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void okWithCodeAndMsg() {
        Mono<R<String>> result = R.ok(1000, "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void okWithDataCodeAndMsg() {
        Mono<R<String>> result = R.ok("data", 1000, "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void errorThrowable() {
        assertThrows(RuntimeException.class, () -> R.error(new RuntimeException("error")).block());
    }

    @Test
    void errorServerException() {
        assertThrows(ServerException.class, () -> R.error(new ServerException("error")).block());
    }

    @Test
    void failedFlux() {
        Mono<R<List<String>>> result = R.failed(Flux.just("data1", "data2"));
        R<List<String>> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.ERROR_CODE, r.getCode());
        assertEquals(ServerException.DEFAULT_MSG, r.getMessage());
        assertEquals(List.of("data1", "data2"), r.getData());
    }

    @Test
    void failedMonoWithMsg() {
        Mono<R<String>> result = R.failed(Mono.just("data"), "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.ERROR_CODE, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void failedFluxWithMsg() {
        Mono<R<List<String>>> result = R.failed(Flux.just("data1", "data2"), "custom message");
        R<List<String>> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.ERROR_CODE, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals(List.of("data1", "data2"), r.getData());
    }

    @Test
    void failedMonoWithCodeAndMsg() {
        Mono<R<String>> result = R.failed(Mono.just("data"), 1000, "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void failedFluxWithCodeAndMsg() {
        Mono<R<List<String>>> result = R.failed(Flux.just("data1", "data2"), 1000, "custom message");
        R<List<String>> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals(List.of("data1", "data2"), r.getData());
    }

    @Test
    void failedEmpty() {
        Mono<R<String>> result = R.failed();
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.ERROR_CODE, r.getCode());
        assertEquals(ServerException.DEFAULT_MSG, r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void failedWithData() {
        Mono<R<String>> result = R.failed("data");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.ERROR_CODE, r.getCode());
        assertEquals(ServerException.DEFAULT_MSG, r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void failedWithDataAndMsg() {
        Mono<R<String>> result = R.failed("data", "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(CommonConstant.ERROR_CODE, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void failedWithCodeAndMsg() {
        Mono<R<String>> result = R.failed(1000, "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void failedWithDataCodeAndMsg() {
        Mono<R<String>> result = R.failed("data", 1000, "custom message");
        R<String> r = result.block();
        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void isSuccess() {
        R<String> successR = new R<>();
        successR.setCode(CommonConstant.SUCCESS_CODE);
        assertTrue(successR.isSuccess());

        R<String> failedR = new R<>();
        failedR.setCode(CommonConstant.ERROR_CODE);
        assertFalse(failedR.isSuccess());
    }

    @Test
    @SuppressWarnings("unchecked")
    void responseCreate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method responseCreateMethod = R.class.getDeclaredMethod("responseCreate", Object.class, int.class, String.class);
        responseCreateMethod.setAccessible(true);

        Mono<R<String>> result = (Mono<R<String>>) responseCreateMethod.invoke(null, "data", 1000, "custom message");
        R<String> r = result.block();

        assertNotNull(r);
        assertEquals(1000, r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals("data", r.getData());
    }

}

