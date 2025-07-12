package co.com.bancolombia.model.movement.gateways;

import co.com.bancolombia.model.movement.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementRepository {
    Flux<Movement> getMovements( String boxId);
    Mono<Movement> putMovement( Movement movement);
}