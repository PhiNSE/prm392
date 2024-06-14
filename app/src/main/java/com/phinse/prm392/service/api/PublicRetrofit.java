package com.phinse.prm392.service.api;

import com.phinse.prm392.service.api.ProductApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PublicRetrofit {
    private static final String BASE_URL = "https://648fd2c81e6aa71680ca1f62.mockapi.io/";
    private static Retrofit retrofit;
    
    //singleton pattern
    private PublicRetrofit() {
    }

    public static Retrofit get() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ProductApi getProductApi() {
        return get().create(ProductApi.class);
    }


}
