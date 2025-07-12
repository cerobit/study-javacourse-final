package co.com.bancolombia.mongo.helper;

import co.com.bancolombia.mongo.MongoDBRepository;
import co.com.bancolombia.mongo.MongoRepositoryAdapter;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.mongo.BoxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private MongoDBRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRepositoryAdapter adapter;

    private Box entity;
    private Flux<Box> entities;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entity = new Box();
        entities = Flux.just(entity);

        BoxData boxData = new BoxData();

        when(objectMapper.map(any(Box.class), eq(BoxData.class))).thenReturn(boxData);
        when(objectMapper.map(any(BoxData.class), eq(Box.class))).thenReturn(entity);

        adapter = new MongoRepositoryAdapter(repository, objectMapper);
    }

    @Test
    void testSave() {
        BoxData boxData = new BoxData();
        when(repository.save(any(BoxData.class))).thenReturn(Mono.just(boxData));

        StepVerifier.create(adapter.save(entity))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void testSaveAll() {
        BoxData boxData = new BoxData();
        when(repository.saveAll(any(Flux.class))).thenReturn(Flux.just(boxData));

        StepVerifier.create(adapter.saveAll(entities))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        BoxData boxData = new BoxData();
        when(repository.findById("key")).thenReturn(Mono.just(boxData));

        StepVerifier.create(adapter.findById("key"))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void testFindByExample() {
        BoxData boxData = new BoxData();
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(boxData));

        StepVerifier.create(adapter.findByExample(entity))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        BoxData boxData = new BoxData();
        when(repository.findAll()).thenReturn(Flux.just(boxData));

        StepVerifier.create(adapter.findAll())
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void testDeleteById() {
        when(repository.deleteById("key")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("key"))
                .verifyComplete();
    }
}