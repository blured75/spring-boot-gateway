# Redirection serverside and url rewritting with rules like nginx & Load Balancing, 
# but better integrated with springboot / springcloud
# Pluggable to discovery services (eureka) & config servers

## Condition on the presence of specific headers 
curl -H "host:prout.spring.io" localhost:9999/hello

curl localhost:9999/hello

## rewrite the query to twitter.com/twitter/@tobozo
localhost:9999/twitter/tobozo 

## observable gateway
See http://localhost:9999/actuator/gateway/routes

## metrics
go to localhost:9999/twitter/tobozo

http://localhost:9999/actuator/metrics

look for key : spring.cloud.gateway.requests

http://localhost:9999/actuator/metrics/spring.cloud.gateway.requests

## Integrate service discovery into gateway
Launch ($ mvn spring-boot:run, don't use your IDE for that) : configServer, Eureka server, ProductJpaConfigClient

Check http://localhost:8761 (ProductJpaConfigClient should automatic register itself toward the eureka server instance)

On getawayServer :

spring.cloud.gateway.discovery.locator.lower-case-service-id=true
spring.cloud.gateway.discovery.locator.enabled=true --> All the entry points for 

## Loadbalanced URI
.route( routeSpec -> routeSpec.path("/product*/**").uri("lb://product")) -> redirect the path /product*/** to
one of the server for the service product

## Service discovery / route events
```
@Bean
ApplicationListener<RefreshRoutesResultEvent> routesRefreshed() {
        return rre -> {
        System.out.println("************ routes updates ************");
        CachingRouteLocator crl = (CachingRouteLocator) rre.getSource();
        Flux<Route> routes = crl.getRoutes();
        routes.subscribe(System.out::println);
    };
}
```

Also via application.yaml

## Integration of the configuration in config server


