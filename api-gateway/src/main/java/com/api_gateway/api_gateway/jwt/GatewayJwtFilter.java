package com.api_gateway.api_gateway.jwt;


import com.example.support_module.jwt.JwtService;
import com.example.support_module.jwt.TokenData;
import com.example.support_module.redis.RedisJwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayJwtFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final RedisJwtService redisJwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);


            if (!jwtService.validateJwtToken(token)) {
                return onError(exchange, "Недействительный токен", HttpStatus.UNAUTHORIZED);
            }


            if (redisJwtService.isTokenBlacklisted(token)) {
                return onError(exchange, "Токен в нигер листе(осуждаю)", HttpStatus.UNAUTHORIZED);
            }

            TokenData tokenData = jwtService.extractTokenData(token);


            if (redisJwtService.isUserBlocked(tokenData.getId())) {
                return onError(exchange, "Пользователь нигер, ой то есть в черном списке", HttpStatus.FORBIDDEN);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        log.error("Заблокирован доступ к {}: {}", exchange.getRequest().getPath(), err);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        response.getHeaders().add(HttpHeaders.CONTENT_TYPE , "application/json");

        Map<String , Object> data = Map.of(
                "message" , err,
                "status" , httpStatus.value(),
                "timestamp" , System.currentTimeMillis()
        );

        try{
            byte[] bytes = objectMapper.writeValueAsBytes(data);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Ошибка при записи JSON-ответа. ", e);
            return response.setComplete();
        }

    }

    @Override
    public int getOrder() {
        return -100;
    }
}




