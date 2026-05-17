package ec.edu.monster.model.service;

import java.util.List;

import ec.edu.monster.model.dto.ClientRequest;
import ec.edu.monster.model.entity.Client;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ClientService {
    @GET("clients")
    Call<List<Client>> getAll();

    @GET("clients/{id}")
    Call<Client> getById(@Path("id") Long id);

    @GET("clients/dni/{dni}")
    Call<Client> getByDni(@Path("dni") String dni);

    @POST("clients")
    Call<Client> create(@Body ClientRequest request);

    @PUT("clients/{id}")
    Call<Client> update(@Path("id") Long id, @Body ClientRequest request);

    @DELETE("clients/{id}")
    Call<Void> delete(@Path("id") Long id);
}
