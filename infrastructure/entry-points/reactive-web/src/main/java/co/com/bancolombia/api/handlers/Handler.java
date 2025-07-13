package co.com.bancolombia.api.handlers;

import co.com.bancolombia.api.model.MovementRequest;
import co.com.bancolombia.api.model.UpdateBoxNameRequest;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.event.BoxEventType;
import co.com.bancolombia.model.event.MovementsUploadEvent;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.MovementType;
import co.com.bancolombia.usecase.getbox.BoxUseCase;
import co.com.bancolombia.usecase.getbox.UploadMovementsUseCase;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.async.api.handlers.EventHandler;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Handler {

    private final BoxUseCase boxUseCase;
    private final UploadMovementsUseCase uploadMovementsUseCase;
    private final EventsGateway eventsGateway;

    public Mono<ServerResponse> getBoxByID(ServerRequest request) {
        String id = request.pathVariable("id");
        return boxUseCase.getBoxByID(id)
                .flatMap(box -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(box))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateBox(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(Box.class)
                .flatMap(box -> ServerResponse.ok().body(boxUseCase.updateBox(id, box), Box.class));
    }

    public Mono<ServerResponse> createBox(ServerRequest request) {
        return request.bodyToMono(Box.class)
                .flatMap(boxUseCase::createBox)
                .flatMap(box -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(box));
    }

    public Mono<ServerResponse> updateBoxName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(UpdateBoxNameRequest.class)
                .flatMap(dto -> boxUseCase.updateBoxName(id, dto.getName()))
                .flatMap(box -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(box))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> closeBox(ServerRequest request) {
        String id = request.pathVariable("id");
        return boxUseCase.CloseBoxByID(id)
                .flatMap(box -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(box))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> reOpenBox(ServerRequest request) {
        String id = request.pathVariable("id");
        return boxUseCase.reOpenBox(id)
                .flatMap(box -> ServerResponse.ok().bodyValue(box))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> deleteBox(ServerRequest request) {
        String id = request.pathVariable("id");
        return boxUseCase.deleteBox(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> listBox(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(boxUseCase.listBox(), Box.class);
    }


    public Mono<ServerResponse> boxMovementsBatch(ServerRequest serverRequest) {
        String boxId = serverRequest.pathVariable("id");

        // Thread-safe counters for success and failure
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);

        MovementsUploadEvent movementsUploadEvent = new MovementsUploadEvent();
        movementsUploadEvent.setBoxId(boxId);
        movementsUploadEvent.setUploadedAt(LocalDateTime.now());
        // Set uploadedBy as needed, e.g., from security context or request

        return serverRequest.multipartData()
                .flatMap(parts -> {
                    FilePart filePart = (FilePart) parts.toSingleValueMap().get("file");
                    if (filePart == null) {
                        return ServerResponse.badRequest().bodyValue("Missing 'file' part in multipart request.");
                    }

                    return filePart.content()
                            .map(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                return new String(bytes, StandardCharsets.UTF_8);
                            })
                            .flatMap(content -> Flux.fromArray(content.split("\\r?\\n")))
                            .filter(line -> !line.trim().isEmpty())
                            .flatMap(line -> {
                                totalCount.incrementAndGet(); // Count total lines

                                return parseLineToMovementRequestReactive(line)
                                        .flatMap(dto -> mapDtoToDomain(dto))
                                        .flatMap(domainMovement -> uploadMovementsUseCase.saveMovement(domainMovement))
                                        .doOnSuccess(saved -> successCount.incrementAndGet())
                                        .doOnError(e -> {
                                            failureCount.incrementAndGet();
                                            System.err.println("Error processing line '" + line + "': " + e.getMessage());
                                        })
                                        .onErrorResume(e -> Mono.empty()); // skip error lines
                            })
                            .then(Mono.defer(() -> {
                                // After all lines processed, build event
                                movementsUploadEvent.setTotal(totalCount.get());
                                movementsUploadEvent.setSuccess(successCount.get());
                                movementsUploadEvent.setFailed(failureCount.get());
                                // Send or publish the event (replace with your event sending logic)

                                return  eventsGateway.emitMovementsUpload(movementsUploadEvent, BoxEventType.FILE_MOVEMENTS_RECIVED)
                                        .thenReturn("File processed for Box ID: " + boxId);
                            }));
                })
                .flatMap(message -> ServerResponse.ok().bodyValue(message))
                .onErrorResume(e -> {
                    System.err.println("Error during file upload: " + e.getMessage());
                    return ServerResponse.status(500).bodyValue("Error processing file: " + e.getMessage());
                });
    }


    public Mono<MovementRequest> parseLineToMovementRequestReactive(String line) {
        return Mono.fromCallable(() -> {
            String[] parts = line.split(",", -1); // Include empty trailing fields if any

            if (parts.length != 7) {
                throw new IllegalArgumentException("Invalid line format: expected 7 fields but got " + parts.length);
            }

            MovementRequest dto = new MovementRequest();
            dto.setMovementId(parts[0].trim());
            dto.setBoxId(parts[1].trim());
            try {
                dto.setDate(LocalDateTime.parse(parts[2].trim())); // ISO 8601 format
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format, must be ISO 8601: " + parts[2]);
            }

            try {
                dto.setType(MovementType.valueOf(parts[3].trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid movement type: " + parts[3]);
            }

            try {
                BigDecimal amount = new BigDecimal(parts[4].trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Amount must be positive");
                }
                dto.setAmount(amount);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount: " + parts[4]);
            }
            dto.setCurrency(parts[5].trim());
            dto.setDescription(parts[6].trim());
            System.out.println(dto.toString());
            return dto;
        });
    }

    public Mono<Movement> mapDtoToDomain(MovementRequest dto) {
        return Mono.fromCallable(() -> {
            Movement movement = new Movement();
            movement.setMovementId(dto.getMovementId());
            movement.setBoxId(dto.getBoxId());
            movement.setDate(dto.getDate());
            movement.setType(dto.getType());
            movement.setAmount(dto.getAmount());
            movement.setCurrency(dto.getCurrency());
            movement.setDescription(dto.getDescription());
            return movement;
        });
    }




}