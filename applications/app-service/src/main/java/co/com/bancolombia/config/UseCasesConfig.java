package co.com.bancolombia.config;

import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import co.com.bancolombia.usecase.getbox.BoxUseCase;
import co.com.bancolombia.usecase.getbox.UploadMovementsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)

public class UseCasesConfig {
        @Bean
        public BoxUseCase boxUseCase(BoxRepository boxRepository, EventsGateway eventsGateway) {
                return new BoxUseCase(boxRepository,eventsGateway );
        }

        @Bean
        public UploadMovementsUseCase uploadMovementsUseCase(BoxRepository boxRepository, MovementRepository movementRepository, EventsGateway eventsGateway) {
                return new UploadMovementsUseCase(boxRepository,movementRepository,eventsGateway );
        }
}
