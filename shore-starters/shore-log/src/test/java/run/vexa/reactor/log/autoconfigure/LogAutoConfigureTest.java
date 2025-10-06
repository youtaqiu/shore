package run.vexa.reactor.log.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import run.vexa.reactor.log.aspect.ApiLogAspect;
import run.vexa.reactor.log.aspect.JoinPointSerialise;
import run.vexa.reactor.log.handler.LogHandler;
import run.vexa.reactor.log.handler.SimpleLogHandler;
import run.vexa.reactor.log.service.ApiLogService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LogAutoConfigureTest {

    @Test
    void shouldCreateCoreBeans() {
        LogAutoConfigure configure = new LogAutoConfigure();

        JoinPointSerialise serialise = configure.joinPointSerialise();
        assertThat(serialise).isNotNull();

        ApiLogAspect aspect = configure.logAspect(serialise);
        assertThat(aspect).isNotNull();

        ObjectProvider<LogHandler> provider = new StaticObjectProvider(Collections.emptyList());
        ApiLogService service = configure.apiLogService(provider);
        assertThat(service).isNotNull();
    }

    @Test
    void shouldCreateConsoleLogHandler() {
        LogConsoleAutoConfiguration configuration = new LogConsoleAutoConfiguration();
        SimpleLogHandler handler = configuration.simpleLogHandler();
        assertThat(handler).isNotNull();
    }

    private record StaticObjectProvider(List<LogHandler> handlers) implements ObjectProvider<LogHandler> {

        @Override
        @NonNull
        public LogHandler getObject(@NonNull Object... args) {
            return handlers.getFirst();
        }

        @Override
        public LogHandler getIfAvailable() {
            return handlers.isEmpty() ? null : handlers.getFirst();
        }

        @Override
        public LogHandler getIfUnique() {
            return handlers.size() == 1 ? handlers.getFirst() : null;
        }

        @Override
        @NonNull
        public Stream<LogHandler> stream() {
            return handlers.stream();
        }

        @Override
        @NonNull
        public Stream<LogHandler> orderedStream() {
            return handlers.stream();
        }
    }
}
