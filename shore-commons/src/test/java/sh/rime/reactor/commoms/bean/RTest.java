package sh.rime.reactor.commoms.bean;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.commons.constants.CommonConstant;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;

import java.util.Arrays;

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
                .expectNextMatches(r -> r.getCode().equals(CommonConstant.SUCCESS_CODE) && r.getData().containsAll(Arrays.asList("data1", "data2")))
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

}

