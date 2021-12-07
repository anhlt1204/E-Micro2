package com.leanh.cloudgateway.filter;

import com.leanh.cloudgateway.exception.JwtTokenMalformedException;
import com.leanh.cloudgateway.exception.JwtTokenMissingException;
import com.leanh.cloudgateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private final RedisTemplate template;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(RedisTemplate template, JwtUtil jwtUtil) {
        this.template = template;
        this.jwtUtil = jwtUtil;
    }

    static Set<String> auths = new HashSet<>();

    static {
        auths.add("login");
        auths.add("register");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String requestUri = request.getURI().getPath();

        if (isAuthenticatedUrl(requestUri) && !isImageUrl(requestUri)) {

            if (!request.getHeaders().containsKey("Authorization")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return response.setComplete();
            }

            final String tokenRaw = request.getHeaders().getOrEmpty("Authorization").get(0);
            String token = extractJwtFromRequest(tokenRaw);

            Object user = template.opsForValue().get(token);
            System.out.println(user);

            if (user == null) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);

                return response.setComplete();
            }

            try {
                jwtUtil.validateToken(token);
            } catch (JwtTokenMalformedException | JwtTokenMissingException e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }

            Claims claims = jwtUtil.getClaims(token);
            exchange.getRequest().mutate().header("isAuth", String.valueOf(claims.get("isAuth"))).build();
            exchange.getRequest().mutate().header("username", String.valueOf(user)).build();
        }

        return chain.filter(exchange);

    }

    private String extractJwtFromRequest(String bearerToken) {
        if (bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isAuthenticatedUrl(String url) {
        for (String a : auths) {
            if (url.contains(a)) {
                return false;
            }
        }
        return true;
    }

    private boolean isImageUrl(String url) {
        return Stream.of(".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG").anyMatch(url::endsWith);
    }

}
