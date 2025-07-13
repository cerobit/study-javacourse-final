package co.com.bancolombia.api.handlers;

import co.com.bancolombia.api.model.MovementRequest;
import co.com.bancolombia.api.model.UpdateBoxNameRequest;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.MovementType;
import co.com.bancolombia.usecase.getbox.BoxUseCase;
import co.com.bancolombia.usecase.getbox.UploadMovementsUseCase;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class Handler {

    private final BoxUseCase boxUseCase;
    private final UploadMovementsUseCase uploadMovementsUseCase;

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
        String id = serverRequest.pathVariable("id");
        // 1. Get the multipart request and extract the file
        return serverRequest.multipartData()
                .flatMap(parts -> {
                    // Assuming the file part is named 'file' in the form
                    // You might need to handle the case where 'file' part is missing
                    FilePart filePart = (FilePart) parts.toSingleValueMap().get("file");
                    if (filePart == null) {
                        return ServerResponse.badRequest().bodyValue("Missing 'file' part in multipart request.");
                    }

                    // 2. Process the file content line by line reactively
                    return filePart.content() // Get Flux<DataBuffer>
                            .map(dataBuffer -> {
                                // Convert DataBuffer to String
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer); // Release the buffer
                                return new String(bytes, StandardCharsets.UTF_8);
                            })
                            .flatMap(content -> {
                                // Split content by lines, handling potential partial lines across DataBuffers
                                // A more robust solution might buffer until a newline is found
                                return Flux.fromArray(content.split("\\r?\\n"));
                            })
                            .filter(line -> !line.trim().isEmpty()) // Filter empty lines
                            .flatMap( line ->
                                parseLineToMovementRequestReactive(line)
                                        .doOnNext(dto -> {
                                            System.out.println("Processing line for Box " + id + ": " + dto);
                                        })
                                        .onErrorResume( e -> {
                                            System.err.println("Skipping invalid line: '" + line + "' due to error: " + e.getMessage());
                                            return Mono.empty();
                                        })
                                        )
                            .then(Mono.just("File processing started for Box ID: " + id)); // Indicate start, actual processing might be async
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

    public Movement toDomain(MovementRequest movementRequest) {
        Movement movement = new Movement();
        movement.setMovementId(movementRequest.getMovementId());
        movement.setBoxId(movementRequest.getBoxId());
        movement.setDate(movementRequest.getDate());
        movement.setType(movementRequest.getType());
        movement.setAmount(movementRequest.getAmount());
        movement.setCurrency(movementRequest.getCurrency());
        movement.setDescription(movementRequest.getDescription());
        return movement;
    }


}