package com.blured.ecomercce.springbootgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGatewayApplication.class, args);
    }

    @Bean
    ApplicationListener<RefreshRoutesResultEvent> routesRefreshed() {
        return rre -> {
            System.out.println("************ routes updates ************");
            CachingRouteLocator crl = (CachingRouteLocator) rre.getSource();
            Flux<Route> routes = crl.getRoutes();
            routes.subscribe(System.out::println);
        };
    }


    @Bean
    RouteLocator gateway (RouteLocatorBuilder rlb) {
        return rlb
                .routes()
                .route(routeSpec -> routeSpec
                        .path("/hello").and().host("*.spring.io")
                        .filters(gatewayFilterSpec ->
                                gatewayFilterSpec.setPath("/guides"))
                        .uri("https://spring.io/")
                )
                .route("twitter", routeSpec -> routeSpec
                        .path("/twitter/**")
                        .filters(fs -> fs.rewritePath("/twitter/(?<handle>.*)",
                        "/${handle}"))
                        .uri("http://twitter.com/@")
                )
                .route( routeSpec -> routeSpec.path("/product*/**").uri("lb://product"))
                .route( routeSpec -> routeSpec.path("/testMessage").uri("lb://product"))
                .build();
    }

}
