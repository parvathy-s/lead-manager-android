package com.example.leadmanager;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("get_user/")
    Call<UserResponse> checkUser(@Body UserRequest userRequest);
}
