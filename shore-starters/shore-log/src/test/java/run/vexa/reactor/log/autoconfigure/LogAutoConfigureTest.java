package run.vexa.reactor.log.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
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

    private static final class StaticObjectProvider implements ObjectProvider<LogHandler> {
        private final List<LogHandler> handlers;

        private StaticObjectProvider(List<LogHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public LogHandler getObject(Object... args) {
            return handlers.get(0);
        }

        @Override
        public LogHandler getIfAvailable() {
            return handlers.isEmpty() ? null : handlers.get(0);
        }

        @Override
        public LogHandler getIfUnique() {
            return handlers.size() == 1 ? handlers.get(0) : null;
        }

        @Override
        public Stream<LogHandler> stream() {
            return handlers.stream();
        }

        @Override
        public Stream<LogHandler> orderedStream() {
            return handlers.stream();
        }
    }
}
