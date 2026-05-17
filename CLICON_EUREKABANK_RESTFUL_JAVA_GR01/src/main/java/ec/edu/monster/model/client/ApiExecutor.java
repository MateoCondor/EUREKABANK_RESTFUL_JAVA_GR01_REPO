package ec.edu.monster.model.client;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import ec.edu.monster.model.exceptions.ApiException;
import retrofit2.Call;
import retrofit2.Response;

public class ApiExecutor {
    public static <T> T execute(Call<T> call, Map<Integer, String> errorMapping) {
        try {
            Response<T> response = call.execute();
            int statusCode = response.code();

            if (response.isSuccessful()) {
                if (statusCode == 204 || response.body() == null)
                    return null;

                return response.body();
            }

            if (errorMapping.containsKey(statusCode))
                throw new ApiException(errorMapping.get(statusCode));

            throw new ApiException("Error inesperado del servidor (Http code: " + statusCode + ")");
        } catch (IOException e) {
            throw new ApiException("Error de comunicación");
        }
    }

    public static <T> T execute(Call<T> call) {
        return execute(call, Collections.emptyMap());
    }
}
