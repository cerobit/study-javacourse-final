package co.com.bancolombia.api.handlers;

import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HandlerRegistryConfiguration {

    @Bean
    @Primary
    public HandlerRegistry handlerRegistry(EventsHandler events) {
        return HandlerRegistry.register()
                .listenEvent("box.event.created", events::handleEventA, Object.class);
    }
}
