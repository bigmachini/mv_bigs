package net.bigmachini.mv_bigs.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Thiago Ferreira (thiago@quimbik.com) on 11/15/16.
 */

public class APIService {
    public static <S> S createService(Class<S> serviceClass, int timespan) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        String url = "";

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);


        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Connection", "close")
                        //.header("X-API-KEY","2eqdZPW3xBvxLGxHyNEezfKKfta4kmPN")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        ConnectionPool connectionPool = new ConnectionPool();
        OkHttpClient client;
        //authorization

        client = httpClient.connectionPool(connectionPool)
                .readTimeout(timespan, TimeUnit.SECONDS)
                .writeTimeout(timespan, TimeUnit.SECONDS)
                .connectTimeout(timespan, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true) //use authenticate as the are calls from cms they can retry
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                //   .baseUrl("http://548f2774.ngrok.io ")
                .baseUrl("http://mvbigs.bigmachini.net:8060")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }
}