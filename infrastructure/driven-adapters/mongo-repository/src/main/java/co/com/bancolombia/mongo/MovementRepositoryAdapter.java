package co.com.bancolombia.mongo;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public class MovementRepositoryAdapter extends AdapterOperations<Movement, MovementData, String, MongoDBMovementRepository>
        implements MovementRepository {

    public MovementRepositoryAdapter(MongoDBMovementRepository repository, ObjectMapper mapper) {
        super(repository, mapper, movementData -> mapper.map(movementData, Movement.class));
    }

    @Override
    public Flux<Movement> getMovements(String boxId) {
        return null;
    }

    @Override
    public Mono<Movement> putMovement(Movement movement) {
        return null;
    }
}

