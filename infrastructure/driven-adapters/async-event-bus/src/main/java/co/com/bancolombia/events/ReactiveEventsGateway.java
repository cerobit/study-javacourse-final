package co.com.bancolombia.events;

import co.com.bancolombia.model.event.BoxEvent;
import co.com.bancolombia.model.event.BoxEventType;
import co.com.bancolombia.model.event.BoxEventUpdate;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@RequiredArgsConstructor
@EnableDomainEventBus
public class ReactiveEventsGateway implements EventsGateway {

    private final DomainEventBus domainEventBus;

    @Override
    public Mono<Void> emit(BoxEvent event, BoxEventType eventType) {
        log.log(Level.INFO, "Sending domain event: {0}: {1}", new String[]{eventType.getValue(), event.toString()});
        return from(domainEventBus.emit(new DomainEvent<>(eventType.getValue(), UUID.randomUUID().toString(), event)));
    }

    @Override
    public Mono<Void> emitEventUpdate(BoxEventUpdate eventUpdate, BoxEventType eventType) {
        log.log(Level.INFO, "Sending domain event: {0}: {1}", new String[]{eventType.getValue(), eventUpdate.toString()});
        return from(domainEventBus.emit(new DomainEvent<>(eventType.getValue(), UUID.randomUUID().toString(), eventUpdate)));
    }
}
