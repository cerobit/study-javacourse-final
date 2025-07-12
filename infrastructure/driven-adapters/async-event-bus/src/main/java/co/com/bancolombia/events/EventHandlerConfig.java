package co.com.bancolombia.events;

import co.com.bancolombia.model.event.BoxEvent;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.api.handlers.DomainEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class EventHandlerConfig {

    @Bean
    public DomainEventHandler<BoxEvent> boxEventHandler() {

        return new DomainEventHandler<>() {
            @Override
            public Mono<Void> handle(DomainEvent<BoxEvent> event) {
                return Mono.fromRunnable(() -> {
                    log.info("Received BoxEvent via reactive-commons: {}", event.getData());
                });
            }
        };
    }

    @Bean
    public HandlerRegistry eventSubscriptions(DomainEventHandler<BoxEvent> boxEventHandler) {
        return HandlerRegistry.register()
                .listenEvent("box.create", boxEventHandler, BoxEvent.class);
    }
}