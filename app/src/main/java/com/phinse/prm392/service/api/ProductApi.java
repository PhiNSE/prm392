package com.phinse.prm392.service.api;

import com.phinse.prm392.service.model.Product;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface ProductApi {
    @GET("product")
    public Call<List<Product>> getProducts();

    @GET("product/{id}")
    public Call<Product> getProductById(String id);

    @GET("product?isInCart=true")
    public Call<List<Product>> getProductsInCart();

    @POST("product")
    public Call<Product> createProduct(Product product);

    @PATCH("product/{id}")
    public Call<Product> updateProduct(String id, Product product);

    @DELETE("product/{id}")
    public Call<JSONObject> deleteProduct(String id);


}
