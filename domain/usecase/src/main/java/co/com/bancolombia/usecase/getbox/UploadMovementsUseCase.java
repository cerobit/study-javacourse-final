package co.com.bancolombia.usecase.getbox;

import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class UploadMovementsUseCase {

    private final BoxRepository boxRepository;
    private final MovementRepository moveMentRepository;
    private final EventsGateway eventsGateway;


    public Flux<Movement> updateBoxName(String id, String name) {
        return Flux.just(Movement.builder().build());
    }
}