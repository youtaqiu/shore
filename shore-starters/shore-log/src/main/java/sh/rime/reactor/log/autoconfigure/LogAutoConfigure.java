package sh.rime.reactor.log.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sh.rime.reactor.log.aspect.ApiLogAspect;
import sh.rime.reactor.log.aspect.JoinPointSerialise;
import sh.rime.reactor.log.handler.LogHandler;
import sh.rime.reactor.log.service.ApiLogService;

/**
 * 日志自动配置
 *
 * @author youta
 **/
@Configuration
public class LogAutoConfigure {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LogAutoConfigure() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * joinPointSerialise
     *
     * @return joinPointSerialise
     */
    @Bean
    @ConditionalOnMissingBean
    public JoinPointSerialise joinPointSerialise() {
        return new JoinPointSerialise();
    }

    /**
     * 日志切面
     *
     * @param joinPointSerialise 切点序列化
     * @return 日志切面
     */
    @Bean
    public ApiLogAspect logAspect(JoinPointSerialise joinPointSerialise) {
        return new ApiLogAspect(joinPointSerialise);
    }

    /**
     * 日志服务
     *
     * @param logHandlersProvider 日志处理器提供者
     * @return 日志服务
     */
    @Bean
    public ApiLogService apiLogService(ObjectProvider<LogHandler> logHandlersProvider) {
        return new ApiLogService(logHandlersProvider);
    }
}
