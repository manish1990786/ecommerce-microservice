package com.manish.ecommerce.payment_service.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
        "/",
        "/health",
        "/actuator",
        "/actuator/info",
        "/actuator/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String servletPath = request.getServletPath();

        if (PUBLIC_PATHS.stream().anyMatch(servletPath::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                URL url = new URL("http://localhost:3001/api/users/verify-token");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", authHeader); // Full Bearer token
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int status = connection.getResponseCode();

                if (status == 200) {
                    Scanner scanner = new Scanner(connection.getInputStream());
                    StringBuilder jsonResponse = new StringBuilder();
                    while (scanner.hasNext()) {
                        jsonResponse.append(scanner.nextLine());
                    }
                    scanner.close();

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(jsonResponse.toString());

                    boolean success = rootNode.path("success").asBoolean(false);

                    if (success) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid token");

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
            }

        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
        }
    }

    public static List<String> getPublicPaths() {
        return PUBLIC_PATHS;
    }
}
