package ec.edu.monster.model.service;

import ec.edu.monster.model.dto.LoginRequest;
import ec.edu.monster.model.dto.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest dto);
}
