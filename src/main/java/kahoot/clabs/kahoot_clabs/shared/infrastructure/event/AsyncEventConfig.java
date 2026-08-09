package kahoot.clabs.kahoot_clabs.shared.infrastructure.event;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncEventConfig implements AsyncConfigurer {

    @Bean(name = "applicationEventTaskExecutor")
    public Executor applicationEventTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("cqrs-projection-");
        executor.setConcurrencyLimit(16);
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return applicationEventTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                org.slf4j.LoggerFactory.getLogger(AsyncEventConfig.class)
                        .error("Async projection failed in {}.{}: {}",
                                method.getDeclaringClass().getSimpleName(),
                                method.getName(),
                                ex.getMessage(),
                                ex);
    }
}
