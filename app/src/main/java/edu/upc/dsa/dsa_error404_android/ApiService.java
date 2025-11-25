package edu.upc.dsa.dsa_error404_android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("game/users/register")
    Call<User> registerUser(@Body Credentials credentials);

    @POST("game/users/login")
    Call<User> loginUser(@Body Credentials credentials);

    @POST("game/users/objects/buy")
    Call<User> comprarItem(@Body CompraRequest request);

    @GET("game/object/list")
    Call<List<GameObject>> getALLGameObjects();
}