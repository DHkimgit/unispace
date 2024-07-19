package com.project.unispace.commons.config;

import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.service.JwtService;
import com.project.unispace.domain.user.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLoginId;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userLoginId = jwtService.extractUsername(jwt);
        System.out.println("userEmail = " + userLoginId);
        if (userLoginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userLoginId);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userLoginId);
            System.out.println("userDetails.getAuthorities() = " + userDetails.getAuthorities());
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("authToken.getAuthorities()" + authToken.getAuthorities());
            }
        }
        filterChain.doFilter(request, response);
    }
}
