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

public class ContactFragment extends Fragment {
    Button save;
    private static final String ARG_USR="usr";
    private String usr;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;

    public static ContactFragment newInstance(String user){
        ContactFragment contactFragment= new ContactFragment();
        Bundle args= new Bundle();
        args.putString(ARG_USR,user);
        contactFragment.setArguments(args);
        System.out.println(user);
        return contactFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =inflater.inflate(R.layout.fragment_contact,container,false);
        if(getArguments() != null) {
            usr = getArguments().getString(ARG_USR);
        }
        save = v.findViewById(R.id.new_con);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        getDetails();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new ContactAdd());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }

    private void getDetails(){
        ArrayList<ContactResponse> contactItems=new ArrayList<>();
        Call<List<ContactResponse>> listCall = ApiClient.getUserService().getContact(usr);
        listCall.enqueue(new Callback<List<ContactResponse>>() {
            @Override
            public void onResponse(Call<List<ContactResponse>> call, Response<List<ContactResponse>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getActivity(),response.code(),Toast.LENGTH_LONG).show();
                    return;
                }

                List<ContactResponse> contactResponses = response.body();
                for(ContactResponse contactResponse: contactResponses){
                    contactItems.add(new ContactResponse(contactResponse.getC_extd__c(),contactResponse.getCname(),contactResponse.getAname(),contactResponse.getTitle()));
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if(contactItems.size() == 0)
                    Toast.makeText(getActivity(),"No contacts present",Toast.LENGTH_LONG).show();

                adapter = new ContactAdapter(contactItems);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String id =contactItems.get(position).getC_extd__c();
                        ContactUpdate contactUpdate = ContactUpdate.newInstance(id);
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,contactUpdate);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<ContactResponse>> call, Throwable t) {
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
