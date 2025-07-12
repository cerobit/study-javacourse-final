package co.com.bancolombia.model.box.gateways;

import co.com.bancolombia.model.box.Box;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BoxRepository {
    Mono<Box> getBoxByID(String id);
    Mono<Box> createBox(Box box);
    Mono<Box> updateBox(String id, Box box);
    Mono<Box> updateName(String id, String name );
    Mono<Void> deleteBox(String id);
    Flux<Box> listBox();
}
