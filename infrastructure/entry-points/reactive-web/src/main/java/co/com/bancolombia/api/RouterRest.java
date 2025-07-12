package co.com.bancolombia.api;

import co.com.bancolombia.api.handlers.Handler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> boxRoutes(Handler handler) {
        return RouterFunctions.route(GET("/box/{id}"), handler::getBoxByID)
                .andRoute(POST("/box"), handler::createBox)
                .andRoute(POST("/box/close/{id}"), handler::closeBox)
                .andRoute(POST("/box/reopen/{id}"), handler::reOpenBox)
                .andRoute(PUT("/box/{id}"), handler::updateBox)
                .andRoute(PATCH("/box/{id}"), handler::updateBoxName)
                .andRoute(DELETE("/box/{id}"), handler::deleteBox)
                .andRoute(GET("/box"), handler::listBox);
    }
}