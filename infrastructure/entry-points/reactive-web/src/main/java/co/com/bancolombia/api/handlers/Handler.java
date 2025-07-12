package co.com.bancolombia.api.handlers;

import co.com.bancolombia.api.model.UpdateBoxNameRequest;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.usecase.getbox.BoxUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final BoxUseCase boxUseCase;

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

}