package sh.rime.reactor.log.autoconfigure;

import sh.rime.reactor.log.aspect.ApiLogAspect;
import sh.rime.reactor.log.handler.LogHandler;
import sh.rime.reactor.log.service.ApiLogService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author youta
 **/
@Configuration
public class LogAutoConfigure {

    /**
     * 日志切面
     *
     * @param apiLogService 日志处理器
     * @param messageSource       国际化
     * @return 日志切面
     */
    @Bean
    public ApiLogAspect logAspect(MessageSource messageSource, ApiLogService apiLogService) {
        return new ApiLogAspect(messageSource, apiLogService);
    }

    /**
     * 日志服务
     * @param logHandlersProvider 日志处理器提供者
     * @return 日志服务
     */
    @Bean
    public ApiLogService apiLogService(ObjectProvider<LogHandler> logHandlersProvider) {
        return new ApiLogService(logHandlersProvider);
    }
}
