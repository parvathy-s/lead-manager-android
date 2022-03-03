package com.example.leadmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private static final String ARG_USR="usr";
    private String usr;
    private TextView txt;
    public static AccountFragment newInstance(String user){
        AccountFragment accountFragment= new AccountFragment();
        Bundle args= new Bundle();
        args.putString(ARG_USR,user);
        accountFragment.setArguments(args);
        System.out.println(user);
        return accountFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_account,container,false);
        txt = v.findViewById(R.id.acc_det);
        if(getArguments() != null) {
            usr = getArguments().getString(ARG_USR);
        }
        getDetails();
        return v;
    }

    private void getDetails(){
        Call<List<AccountResponse>> listCall = ApiClient.getUserService().getAccount(usr);
        listCall.enqueue(new Callback<List<AccountResponse>>() {
            @Override
            public void onResponse(Call<List<AccountResponse>> call, Response<List<AccountResponse>> response) {
                if(!response.isSuccessful()){
                    txt.setText(response.code());
                    return;
                }

                List<AccountResponse> accountResponses = response.body();

                for(AccountResponse accountResponse: accountResponses){
                    String ac="";
                    ac+= accountResponse.getName()+"\n"+accountResponse.getType()+"\n"+accountResponse.getIndustry();
                    ac+="\n\n";
                    txt.append(ac);
                }


            }

            @Override
            public void onFailure(Call<List<AccountResponse>> call, Throwable t) {
                txt.setText(t.getLocalizedMessage());
            }
        });
    }
}
