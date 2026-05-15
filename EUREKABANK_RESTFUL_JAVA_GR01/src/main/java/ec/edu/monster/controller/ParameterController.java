package ec.edu.monster.controller;

import ec.edu.monster.dto.ParameterDTO;
import ec.edu.monster.service.ParameterService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/parameters")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ParameterController {

    @Inject
    private ParameterService parameterService;

    @GET
    public Response getAll() {
        List<ParameterDTO> parameters = parameterService.getAllParameters();
        return Response.ok(parameters).build();
    }

    @GET
    @Path("/{key}")
    public Response getByKey(@PathParam("key") String key) {
        ParameterDTO parameter = parameterService.getByKey(key);
        return Response.ok(parameter).build();
    }

    @POST
    public Response create(ParameterDTO request) {
        ParameterDTO created = parameterService.createParameter(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ParameterDTO request) {
        ParameterDTO updated = parameterService.updateParameter(id, request);
        return Response.ok(updated).build();
    }
}
