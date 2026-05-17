package ec.edu.monster.model.service;

import java.util.List;

import ec.edu.monster.model.dto.DepositRequest;
import ec.edu.monster.model.dto.TransferRequest;
import ec.edu.monster.model.dto.WithdrawRequest;
import ec.edu.monster.model.entity.Transaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TransactionService {
    @GET("transactions/account/{accountId}")
    Call<List<Transaction>> getByAccount(@Path("accountId") Long accountId);

    @POST("transactions/deposit")
    Call<Transaction> deposit(@Body DepositRequest request);

    @POST("transactions/withdraw")
    Call<Transaction> withdraw(@Body WithdrawRequest request);

    @POST("transactions/transfer")
    Call<Transaction> transfer(@Body TransferRequest request);
}
