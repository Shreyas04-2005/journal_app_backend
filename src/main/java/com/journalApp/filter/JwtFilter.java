package com.journalApp.filter;

import com.journalApp.utils.JwtUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private final Map<String, Bucket>buckets=new ConcurrentHashMap<>();

    //bucket for login endpoint
    private Bucket loginBucket(){
    return Bucket.builder()
            .addLimit(Bandwidth.simple(3, Duration.ofMinutes(1)))
            .build();
    }

    //bucket for all default endpoint
    private Bucket defaultBucket(){
        return Bucket.builder()
                .addLimit(Bandwidth.simple(30,Duration.ofMinutes(1)))
                .build();
    }

    //bucket resolver
    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> {
            if (k.endsWith(":login")) {
                return loginBucket();
            } else {
                return defaultBucket();
            }
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path=request.getRequestURI();
        boolean isLogin=path.endsWith("/login"); //login endpoint

        //extract jwt
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        //Extract token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
               //handle by validation
            }
        }

        String key;
        String clientId = (username != null) ? username : request.getRemoteAddr();
        if (isLogin) {
            key = request.getRemoteAddr(); // always IP for login
        } else {
            key = clientId; // stable fallback
        }
        String bucketKey = key + (isLogin ? ":login" : ":default");

        Bucket bucket = getBucket(bucketKey);

        if(!bucket.tryConsume(1)){
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests try again in minute\"}");
            return;
        }

        try {
            // Set authentication if valid and not already set
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            // continue request
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Token expired. Please login again.\"}");
        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid token\"}");
        }
    }
}