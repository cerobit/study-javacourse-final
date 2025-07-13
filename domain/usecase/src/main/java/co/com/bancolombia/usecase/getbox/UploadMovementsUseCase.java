package co.com.bancolombia.usecase.getbox;

import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UploadMovementsUseCase {

    private final BoxRepository boxRepository;
    private final MovementRepository moveMentRepository;
    private final EventsGateway eventsGateway;

    public Mono<Movement> saveMovement(Movement movement) {
        return moveMentRepository.putMovement(movement);
    }
}