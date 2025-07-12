package co.com.bancolombia.mongo;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoRepositoryAdapter extends AdapterOperations<Box, BoxData, String, MongoDBRepository>
        implements BoxRepository {

    public MongoRepositoryAdapter(MongoDBRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Box.class));
    }

    public Mono<Box> findById(String id) {
        return repository.findById(id).map(this::toEntity);
    }

    public Mono<Box> save(Box box) {
        BoxData boxObject = new BoxData();
        boxObject.setName(box.getName());
        boxObject.setClosedAt(box.getClosedAt());
        boxObject.setId(box.getId());
        boxObject.setClosingAmount(box.getClosingAmount());
        boxObject.setOpenedAt(box.getOpenedAt());
        boxObject.setStatus(box.getStatus());
        boxObject.setCurrentBalance(box.getCurrentBalance());
        boxObject.setClosedAt(box.getClosedAt());
        return repository.save(boxObject).map(this::toEntity);
    }

    @Override
    public Mono<Box> updateName(String id, String name) {
        return repository.findById(id)
                .flatMap(boxData -> {
                    boxData.setName(name);
                    return repository.save(boxData);
                })
                .map(this::toEntity);
    }


    @Override
    public Mono<Box> getBoxByID(String id) {
        return findById(id);
    }

    @Override
    public Mono<Box> createBox(Box box) {
        return save(box);
    }

    @Override
    public Mono<Box> updateBox(String id, Box box) {
        box.setId(id);
        return repository.existsById(id)
                .flatMap(exists -> exists ? save(box) : Mono.empty());
    }

    @Override
    public Mono<Void> deleteBox(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Box> listBox() {
        return repository.findAll().map(this::toEntity);
    }
}