package com.example.leadmanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private static final String ARG_USR="usr";
    private String usr;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button add;
    private long backPressedTime;
    private Toast back;
    private ProgressDialog progressDialog;

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
        setHasOptionsMenu(true);
        View v =inflater.inflate(R.layout.fragment_account,container,false);
        if(getArguments() != null) {
            usr = getArguments().getString(ARG_USR);
        }

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        getDetails();

        add= v.findViewById(R.id.new_acc);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new AccountAdd());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(backPressedTime + 2000 > System.currentTimeMillis()){
                    back.cancel();
                    getActivity().finishAffinity();
                } else{
                    back =Toast.makeText(getActivity(),"Press again to exit",Toast.LENGTH_SHORT);
                    back.show();
                }
                backPressedTime  = System.currentTimeMillis();
            }
        });
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

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if(accountItems.size() == 0)
                    Toast.makeText(getActivity(),"No accounts present",Toast.LENGTH_LONG).show();

                adapter = new AccountAdapter(accountItems);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String id =accountItems.get(position).getId();
                        AccountUpdate accountUpdate = AccountUpdate.newInstance(id);
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,accountUpdate);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<AccountResponse>> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.lead_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView= (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return;
    }
}
