package ec.edu.monster.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * JAX-RS filter that adds CORS headers to every response so that
 * browser-based clients (React Native Web, etc.) on a different
 * origin can consume the REST API.
 *
 * It also intercepts OPTIONS preflight requests and returns 200
 * immediately, preventing them from reaching the actual resource.
 */
@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // If it is a preflight (OPTIONS) request, abort with 200 right away
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(
                Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Origin, X-Requested-With")
                    .header("Access-Control-Max-Age", "86400")
                    .build()
            );
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers",
                "Content-Type, Authorization, Accept, Origin, X-Requested-With");
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", "86400");
    }
}
