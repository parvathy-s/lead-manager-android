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

public class OpportunityFragment extends Fragment {
    Button save;
    private static final String ARG_USR="usr";
    private String usr;
    private RecyclerView recyclerView;
    private OpportunityAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;

    public static OpportunityFragment newInstance(String user){
        OpportunityFragment opportunityFragment= new OpportunityFragment();
        Bundle args= new Bundle();
        args.putString(ARG_USR,user);
        opportunityFragment.setArguments(args);
        System.out.println(user);
        return opportunityFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =inflater.inflate(R.layout.fragment_opportunity,container,false);
        if(getArguments() != null) {
            usr = getArguments().getString(ARG_USR);
        }
        save = v.findViewById(R.id.new_opportunity);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        getData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new OpportunityAdd());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    private void getData(){
        ArrayList<OpportunityResponse> opportunityItems= new ArrayList<>();
        Call<List<OpportunityResponse>> listCall= ApiClient.getUserService().getOpportunity(usr);
        listCall.enqueue(new Callback<List<OpportunityResponse>>() {
            @Override
            public void onResponse(Call<List<OpportunityResponse>> call, Response<List<OpportunityResponse>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getActivity(),response.code(),Toast.LENGTH_LONG).show();
                    return;
                }

                List<OpportunityResponse> opportunityResponses = response.body();
                for(OpportunityResponse opportunityResponse: opportunityResponses){
                    opportunityItems.add(new OpportunityResponse(opportunityResponse.getO_extid__c(),opportunityResponse.getOname(),opportunityResponse.getAname(),opportunityResponse.getStagename()));
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if(opportunityItems.size() == 0)
                    Toast.makeText(getActivity(),"No Opportunities present",Toast.LENGTH_LONG).show();
                adapter = new OpportunityAdapter(opportunityItems);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new OpportunityAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String id =opportunityItems.get(position).getO_extid__c();
                        OpportunityUpdate opportunityUpdate=OpportunityUpdate.newInstance(id);
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,opportunityUpdate);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<OpportunityResponse>> call, Throwable t) {
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
