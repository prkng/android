package ng.prk.prkngandroid.io;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpErrorInterceptor implements Interceptor {
    private final static String TAG = "HttpErrorInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);

        if (!response.isSuccessful()) {
            throw new PrkngApiError(response.code(), response.message());
        }

        return response;
    }
}
