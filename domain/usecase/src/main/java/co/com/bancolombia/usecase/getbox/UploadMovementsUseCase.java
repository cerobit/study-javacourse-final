package co.com.bancolombia.usecase.getbox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.event.BoxEvent;
import co.com.bancolombia.model.event.BoxEventType;
import co.com.bancolombia.model.event.BoxEventUpdate;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UploadMovementsUseCase {

    private final BoxRepository boxRepository;
    private final MoveMentsRepository moveMentsRepository;
    private final EventsGateway eventsGateway;


    public Mono<Box> updateBoxName(String id, String name) {
        return Mono.defer(() -> {
            if (name == null || name.trim().isEmpty()) {
                return Mono.error(new IllegalArgumentException("Name must not be empty"));
            }
            return boxRepository.getBoxByID(id)
                    .flatMap(boxBefore ->
                            boxRepository.updateName(id, name)
                                    .flatMap(updatedBox -> {
                                        BoxEventUpdate event = new BoxEventUpdate().toBuilder()
                                                .previousName(boxBefore.getName())
                                                .newName(name)
                                                .updatedAt(new java.sql.Timestamp(System.currentTimeMillis()))
                                                .build();
                                        return eventsGateway.emitEventUpdate(event, BoxEventType.UPDATE).thenReturn(updatedBox);
                                    })
                                    .onErrorResume(e -> {
                                        BoxEventUpdate event = new BoxEventUpdate().toBuilder()
                                                .previousName(boxBefore.getName())
                                                .newName(name)
                                                .updatedAt(new java.sql.Timestamp(System.currentTimeMillis()))
                                                .build();
                                        return eventsGateway.emitEventUpdate(event, BoxEventType.UPDATE).then(Mono.error(e));
                                    })
                    );
        });
    }

}