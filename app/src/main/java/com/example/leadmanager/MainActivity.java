package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText username,password;
    public String usr,pass,fname,lname,email,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=(EditText) findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        usr = username.getText().toString();
        pass = password.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        String isLoggedIn = sessionManagement.getSession();
        if(isLoggedIn.equals("null")){

        }
        else
            openHomePage();
    }

    private void openHomePage() {
        Intent intent = new Intent(MainActivity.this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onCheckLogin(View v){

        //Toast.makeText(this, "Username :"+username.getText().toString()+" Password :"+password.getText().toString(), Toast.LENGTH_SHORT).show();
        checkUser(createRequest());
    }

    public UserRequest createRequest(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(username.getText().toString());
        userRequest.setPassword(password.getText().toString());
        return userRequest;
    }

    public void checkUser(UserRequest userRequest)
    {
        Call<UserResponse> userResponseCall = ApiClient.getUserService().checkUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Login success",Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        usr = jsonObject.getString("username");
                        fname = jsonObject.getString("firstname");
                        lname = jsonObject.getString("lastname");
                        email = jsonObject.getString("email");
                        phone = jsonObject.getString("phone");
                        //Toast.makeText(MainActivity.this,usr+"\n"+fname+"\n"+lname+"\n"+email+"\n"+phone,Toast.LENGTH_LONG).show();
                        UserResponse user = new UserResponse();
                        user.setUsername(usr);
                        user.setFirstname(fname);
                        user.setLastname(lname);
                        user.setEmail(email);
                        user.setPhone(phone);
                        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                        sessionManagement.saveSession(user);
                        openHomePage();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(MainActivity.this,"Login failed",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Request failed "+ t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}