package ng.prk.prkngandroid.io;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

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
