package com.example.leadmanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
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

public class LeadFragment extends Fragment {
    private static final String ARG_USR="usr";
    private String usr;
    private RecyclerView recyclerView;
    private LeadAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<LeadResponse> leadItems;
    private Button add;
    private ProgressDialog progressDialog;

    public static LeadFragment newInstance(String user){
        LeadFragment leadFragment= new LeadFragment();
        Bundle args= new Bundle();
        args.putString(ARG_USR,user);
        leadFragment.setArguments(args);
        System.out.println(user);
        return leadFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =inflater.inflate(R.layout.fragment_lead,container,false);
        if(getArguments() != null) {
            usr = getArguments().getString(ARG_USR);
        }
        add = v.findViewById(R.id.new_lead);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getDetails();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new LeadAdd());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }


    private void getDetails() {
        leadItems = new ArrayList<>();
        Call<List<LeadResponse>> call = ApiClient.getUserService().getLead(usr);
        call.enqueue(new Callback<List<LeadResponse>>() {
            @Override
            public void onResponse(Call<List<LeadResponse>> call, Response<List<LeadResponse>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<LeadResponse> leadResponses = response.body();
                for (LeadResponse leadResponse : leadResponses) {
                    leadItems.add(new LeadResponse(
                            leadResponse.getL_extid__c(),
                            leadResponse.getName(),
                            leadResponse.getCompany(),
                            leadResponse.getStatus()
                    ));
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if(leadItems.size() == 0)
                    Toast.makeText(getActivity(),"No leads present",Toast.LENGTH_LONG).show();

                adapter = new LeadAdapter(leadItems);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new LeadAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String id = leadItems.get(position).getL_extid__c();
                        System.out.println(id);
                        LeadUpdate leadUpdate = LeadUpdate.newInstance(id);
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, leadUpdate);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<LeadResponse>> call, Throwable t) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
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
