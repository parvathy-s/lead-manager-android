package com.example.leadmanager;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @POST("get_user/")
    Call<UserResponse> checkUser(@Body UserRequest userRequest);

    @GET("get_user/{id}/")
    Call<UserInfo> getUser(@Path("id") String usr);

    @GET("get_account/{id}")
    Call<List<AccountResponse>> getAccount(@Path("id") String usr);
}
