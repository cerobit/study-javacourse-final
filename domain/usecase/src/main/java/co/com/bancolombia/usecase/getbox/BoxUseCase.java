package co.com.bancolombia.usecase.getbox;

import co.com.bancolombia.model.event.BoxEvent;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.event.BoxEventType;
import co.com.bancolombia.model.event.BoxEventUpdate;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class BoxUseCase {
    private final BoxRepository boxRepository;
    private final EventsGateway eventsGateway;

    public Mono<Box> getBoxByID(String id) {
        return boxRepository.getBoxByID(id);
    }

    public Mono<Box> createBox(Box box) {
        return boxRepository.createBox(box)
                .flatMap(createdBox -> {
                    BoxEvent event = new BoxEvent("CREATE", "SUCCESS", createdBox );
                    return eventsGateway.emit(event, BoxEventType.CREATE ).thenReturn(createdBox);
                })
                .onErrorResume(e -> {
                    BoxEvent event = new BoxEvent( "CREATE", "FAILED", box);
                    return eventsGateway.emit(event, BoxEventType.CREATE).then(Mono.error(e));
                });
    }

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

public Mono<Box> CloseBoxByID(String id) {
    return boxRepository.getBoxByID(id)
        .flatMap(box -> {
            if (box.getStatus() != co.com.bancolombia.model.boxstatus.BoxStatus.OPENED) {
                return Mono.error(new IllegalStateException("La caja no está abierta"));
            }
            box.setClosingAmount(box.getCurrentBalance());
            box.setClosedAt(java.time.LocalDateTime.now());
            box.setStatus(co.com.bancolombia.model.boxstatus.BoxStatus.CLOSED);
            return boxRepository.updateBox(id, box)
                    .flatMap(updatedBox -> {
                        BoxEvent event = new BoxEvent("CLOSE", "SUCCESS", updatedBox);
                        return eventsGateway.emit(event, BoxEventType.CLOSE).thenReturn(updatedBox);
                    });
        })
        .onErrorResume(e -> {
            BoxEvent event = new BoxEvent("CLOSE", "FAILED", new Box(id, null, null, null, null, null, null, null));
            return eventsGateway.emit(event, BoxEventType.CLOSE)
                    .then(Mono.error(e));
        });
}

    public Mono<Box> reOpenBox(String id) {
        return boxRepository.getBoxByID(id)
                .flatMap(box -> {
                    if (box.getStatus() != co.com.bancolombia.model.boxstatus.BoxStatus.CLOSED) {
                        BoxEvent event = new BoxEvent("REOPEN", "FAILED", box);
                        return eventsGateway.emit(event, BoxEventType.RE_OPEN)
                                .then(Mono.error(new IllegalStateException("The box is not closed")));
                    }
                    box.setStatus(co.com.bancolombia.model.boxstatus.BoxStatus.OPENED);
                    box.setClosedAt(null);
                    box.setClosingAmount(null);
                    return boxRepository.updateBox(id, box)
                            .flatMap(updatedBox -> {
                                BoxEvent event = new BoxEvent("REOPEN", "SUCCESS", updatedBox);
                                return eventsGateway.emit(event, BoxEventType.RE_OPEN).thenReturn(updatedBox);
                            });
                })
                .onErrorResume(e -> {
                    BoxEvent event = new BoxEvent("REOPEN", "FAILED", new Box(id, null, null, null, null, null, null, null));
                    return eventsGateway.emit(event, BoxEventType.RE_OPEN)
                            .then(Mono.error(e));
                });
    }

    public Mono<Box> updateBox(String id, Box box) {
        return boxRepository.updateBox(id, box);
    }

    public Mono<Void> deleteBox(String id) {
        return boxRepository.deleteBox(id);
    }

    public Flux<Box> listBox() {
        return boxRepository.listBox();
    }

}