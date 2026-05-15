package ec.edu.monster.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HomeController {

    @GET
    public Response home() {
        Map<String, String> payload = Map.of(
                "name", "EUREKABANK REST API",
                "status", "UP",
                "loginEndpoint", "/auth/login"
        );
        return Response.ok(payload).build();
    }
}
