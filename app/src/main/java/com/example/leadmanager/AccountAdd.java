package com.example.leadmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAdd extends Fragment implements AdapterView.OnItemSelectedListener {
    Button save;
    Spinner type, industry;
    EditText name, phone, desc;
    String isLoggedIn;
    AccountFragment accountFragment;
    FragmentTransaction fragmentTransaction;
    String typesel,industrysel, namesel,descsel, phonesel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_add,container,false);
        save = v.findViewById(R.id.accsave);
        type = v.findViewById(R.id.acctype);
        industry = v.findViewById(R.id.accindustry);
        name = v.findViewById(R.id.accname);
        phone = v.findViewById(R.id.accphone);
        desc = v.findViewById(R.id.accdesc);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getContext(),R.array.account_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
        type.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> industryAdapter = ArrayAdapter.createFromResource(getContext(),R.array.account_industry, android.R.layout.simple_spinner_item);
        industryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        industry.setAdapter(industryAdapter);
        industry.setOnItemSelectedListener(this);

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        accountFragment = AccountFragment.newInstance(isLoggedIn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namesel = name.getText().toString();
                descsel = desc.getText().toString();
                phonesel = phone.getText().toString();

                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,accountFragment);
                fragmentTransaction.addToBackStack(null);


                if (namesel.length() == 0 || descsel.length() == 0 || phonesel.length() == 0)
                    Toast.makeText(getActivity(),"Enter all values",Toast.LENGTH_LONG).show();
                else if(phonesel.length() != 10)
                    Toast.makeText(getActivity(),"Invalid phone",Toast.LENGTH_LONG).show();
                else {
                    apiCall();
                }


            }
        });
        return v;
    }

    private void apiCall(){
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setName(namesel);
        accountRequest.setPhone(phonesel);
        accountRequest.setType(typesel);
        accountRequest.setDescription(descsel);
        accountRequest.setIndustry(industrysel);

        Call<ApiStatus> call = ApiClient.getUserService().createAccount(accountRequest);
        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),"Successfully saved",Toast.LENGTH_LONG).show();
                    fragmentTransaction.commit();
                }
                else {
                    Toast.makeText(getActivity(),"Cannot save "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.acctype:
                typesel = parent.getItemAtPosition(position).toString();
                break;
            case R.id.accindustry:
                industrysel = parent.getItemAtPosition(position).toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
