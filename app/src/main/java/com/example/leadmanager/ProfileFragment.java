package com.example.leadmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String ARG_USR="usr";
    private String usr;
    private TextView txt;
    private TextView username,firstname,lastname,email,phone;
    private ProgressDialog progressDialog;

    public static ProfileFragment newInstance(String user){
        ProfileFragment profileFragment= new ProfileFragment();
        Bundle args= new Bundle();
        args.putString(ARG_USR,user);
        profileFragment.setArguments(args);
        return profileFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_profile,container,false);
        username = v.findViewById(R.id.username);
        firstname = v.findViewById(R.id.fname);
        lastname = v.findViewById(R.id.lname);
        email = v.findViewById(R.id.email);
        phone= v.findViewById(R.id.phone);
        txt = v.findViewById(R.id.usrTxt);

        if(getArguments() != null){
            usr = getArguments().getString(ARG_USR);
            username.setText(usr);
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getDetails();
        return v;
    }

    private void getDetails(){
        Call<UserInfo> userInfoCall = ApiClient.getUserService().getUser(usr);
        userInfoCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()){
                    try{
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        firstname.setText(jsonObject.getString("firstname"));
                        lastname.setText(jsonObject.getString("lastname"));
                        email.setText(jsonObject.getString("email"));
                        phone.setText(jsonObject.getString("phone"));
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                {
                    txt.setText(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                txt.setText(t.getLocalizedMessage());
            }
        });
    }
}
