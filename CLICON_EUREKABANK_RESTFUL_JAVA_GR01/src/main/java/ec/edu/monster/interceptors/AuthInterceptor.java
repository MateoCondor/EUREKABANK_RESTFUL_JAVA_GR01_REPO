package ec.edu.monster.interceptors;

import java.io.IOException;

import ec.edu.monster.model.entity.UserSession;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String token = UserSession.getInstance().getToken();

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        Request authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(authorizedRequest);
    }

}
