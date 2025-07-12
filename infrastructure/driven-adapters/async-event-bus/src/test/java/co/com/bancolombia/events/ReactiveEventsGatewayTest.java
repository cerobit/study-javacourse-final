package co.com.bancolombia.events;

import co.com.bancolombia.model.event.BoxEvent;
import co.com.bancolombia.model.event.BoxEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.api.domain.DomainEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReactiveEventsGatewayTest {

    @Mock
    private DomainEventBus domainEventBus;

    private ReactiveEventsGateway reactiveEventsGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reactiveEventsGateway = new ReactiveEventsGateway(domainEventBus);
    }

    @Test
    void testEmitLogsEvent() {
        BoxEvent event = mock(BoxEvent.class);
        BoxEventType eventType = BoxEventType.CREATE;

        when(domainEventBus.emit(any(DomainEvent.class))).thenReturn(Mono.empty());

        reactiveEventsGateway.emit(event, eventType).block();

        verify(domainEventBus, times(1)).emit(any(DomainEvent.class));
    }

    @Test
    void testEmitConstructsDomainEvent() {
        BoxEvent event = mock(BoxEvent.class);
        BoxEventType eventType = BoxEventType.CREATE;

        when(domainEventBus.emit(any(DomainEvent.class))).thenReturn(Mono.empty());

        reactiveEventsGateway.emit(event, eventType).block();

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(domainEventBus, times(1)).emit(eventCaptor.capture());

        DomainEvent capturedEvent = eventCaptor.getValue();
        assertEquals(eventType.getValue(), capturedEvent.getName());
        assertEquals(event, capturedEvent.getData());
    }
}