package com.example.leadmanager;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @POST("get_user/")
    Call<UserResponse> checkUser(@Body UserRequest userRequest);

    @GET("get_user/{id}/")
    Call<UserInfo> getUser(@Path("id") String usr);

    //account
    @GET("get_account/{id}")
    Call<List<AccountResponse>> getAccount(@Path("id") String usr);

    @POST("save_account/")
    Call<ApiStatus> createAccount(@Body AccountRequest accountRequest);

    @GET("account_info/{id}")
    Call<AccountRequest> accountDetails(@Path("id") String id);

    @PUT("update_account/{id}")
    Call<ApiStatus> updateAccount(@Path("id") String id, @Body AccountRequest accountRequest);

    @POST("delete_account/{id}")
    Call<ApiStatus> deleteAccount(@Path("id") String id);

    //contact
    @GET("get_contact/{id}")
    Call<List<ContactResponse>> getContact(@Path("id") String usr);

    @GET("account_list/{id}")
    Call<List<ContactAccount>> accountList(@Path("id") String usr);

    @POST("save_contact/")
    Call<ApiStatus> createContact(@Body ContactRequest contactRequest);

    @GET("contact_info/{id}")
    Call<ContactRequest> contactDetails(@Path("id") String id);

    @PUT("update_contact/{id}")
    Call<ApiStatus> updateContact(@Path("id") String id, @Body ContactRequest contactRequest);

    @POST("delete_contact/{id}")
    Call<ApiStatus> deleteContact(@Path("id") String id);
}
