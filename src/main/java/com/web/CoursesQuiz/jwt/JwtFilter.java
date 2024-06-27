package com.web.CoursesQuiz.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.UserRepository;
import com.web.CoursesQuiz.user.service.UserDetailsServiceImpl;

@Setter
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired 
    private UserRepository userRepository;


    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // "/", "/error", "/webjars/**", "/index.html", "/signup", "/signin"
        return path.startsWith("/webjars") || path.startsWith("/index.html") || path.startsWith("/api/signup") || path.startsWith("/api/verifyemail")
                || path.startsWith("/api/signin") ||
                path.equals("/api") || path.equals("/api/welcome.html") 
                || path.startsWith("/api/refreshtoken") 
                || path.startsWith("/swagger-ui.html") || path.startsWith("/swagger-resources")
                || path.startsWith("/v2/api-docs") || path.startsWith("/v3/api-docs") ||
                path.startsWith("/configuration/ui") || path.startsWith("/configuration/security")
                || path.startsWith("/swagger-ui") || path.startsWith("/webjars") || path.startsWith("/pay.html");
    }

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token does not begin with Bearer String");
            return;
        }
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUserName(jwt);
        if(userEmail != null)
        {
            User user = userRepository.findByEmail(userEmail);
            if(user == null)
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }
            
            if(!user.isEmailVerified())
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email not verified");
                return;
            }
        }
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is not valid");
                return;
            }
            filterChain.doFilter(request, response);
        }
    }

}
