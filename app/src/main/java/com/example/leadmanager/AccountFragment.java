package com.example.leadmanager;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private static final String ARG_USR="usr";
    private String usr;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button add;


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
        if(getArguments() != null) {
            usr = getArguments().getString(ARG_USR);
        }
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        getDetails();

        add= v.findViewById(R.id.new_acc);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new AccountAdd());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(),"Calling fragment",Toast.LENGTH_LONG).show();
                fragmentTransaction.commit();
            }
        });
        return v;
    }

    private void getDetails(){

        ArrayList<AccountItem> accountItems=new ArrayList<>();
        Call<List<AccountResponse>> listCall = ApiClient.getUserService().getAccount(usr);
        listCall.enqueue(new Callback<List<AccountResponse>>() {
            @Override
            public void onResponse(Call<List<AccountResponse>> call, Response<List<AccountResponse>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getActivity(),response.code(),Toast.LENGTH_LONG).show();
                    return;
                }

                List<AccountResponse> accountResponses = response.body();
                for(AccountResponse accountResponse: accountResponses){
                    accountItems.add(new AccountItem(accountResponse.getName(),accountResponse.getAc_extid__c(),accountResponse.getType(),accountResponse.getIndustry()));
                }

                adapter = new AccountAdapter(accountItems);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<AccountResponse>> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}
