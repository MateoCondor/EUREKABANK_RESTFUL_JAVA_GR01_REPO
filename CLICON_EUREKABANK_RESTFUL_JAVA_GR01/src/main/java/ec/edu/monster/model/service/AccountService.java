package ec.edu.monster.model.service;

import java.util.List;

import ec.edu.monster.model.dto.AccountBalanceResponse;
import ec.edu.monster.model.dto.AccountRequest;
import ec.edu.monster.model.dto.AccountStatusRequest;
import ec.edu.monster.model.entity.Account;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AccountService {
    @GET("accounts")
    Call<List<Account>> getAll();

    @GET("accounts/{id}")
    Call<Account> getById(@Path("id") Long id);

    @GET("accounts/{id}/balance")
    Call<AccountBalanceResponse> getBalance(@Path("id") Long id);

    @GET("accounts/client/{clientId}")
    Call<List<Account>> getByClientId(@Path("clientId") Long clientId);

    @POST("products")
    Call<Account> create(@Body AccountRequest dto);

    @PUT("products/{id}/status")
    Call<Account> updateStatus(@Path("id") Long id, @Body AccountStatusRequest dto);
}
