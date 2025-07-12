package co.com.bancolombia.model.events.gateways;

import co.com.bancolombia.model.event.BoxEvent;
import co.com.bancolombia.model.event.BoxEventType;
import co.com.bancolombia.model.event.BoxEventUpdate;
import reactor.core.publisher.Mono;

public interface EventsGateway {
    Mono<Void> emit(BoxEvent event, BoxEventType eventType);
    Mono<Void> emitEventUpdate(BoxEventUpdate event, BoxEventType eventType);
}
