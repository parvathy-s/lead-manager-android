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

import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountUpdate extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String ARG_ID="id";
    Button update, delete;
    Spinner type, industry;
    EditText name, phone, desc;
    String isLoggedIn, accid;
    AccountFragment accountFragment;
    FragmentTransaction fragmentTransaction;
    String typesel,industrysel, namesel,descsel, phonesel;
    ArrayAdapter<CharSequence> typeAdapter,industryAdapter;

    public static AccountUpdate newInstance(String accid){
        AccountUpdate accountUpdate= new AccountUpdate();
        Bundle args= new Bundle();
        args.putString(ARG_ID,accid);
        accountUpdate.setArguments(args);
        System.out.println(accid);
        return accountUpdate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_update,container,false);

        update = v.findViewById(R.id.accupdate);
        type = v.findViewById(R.id.acctype);
        industry = v.findViewById(R.id.accindustry);
        name = v.findViewById(R.id.accname);
        phone = v.findViewById(R.id.accphone);
        desc = v.findViewById(R.id.accdesc);
        delete = v.findViewById(R.id.accdelete);

        if(getArguments() != null) {
            accid = getArguments().getString(ARG_ID);
        }

        typeAdapter = ArrayAdapter.createFromResource(getContext(),R.array.account_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
        type.setOnItemSelectedListener(this);

        industryAdapter = ArrayAdapter.createFromResource(getContext(),R.array.account_industry, android.R.layout.simple_spinner_item);
        industryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        industry.setAdapter(industryAdapter);
        industry.setOnItemSelectedListener(this);

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        accountFragment = AccountFragment.newInstance(isLoggedIn);

        getData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namesel = name.getText().toString();
                descsel = desc.getText().toString();
                phonesel = phone.getText().toString();
                AccountRequest request = new AccountRequest();
                request.setName(namesel);
                request.setPhone(phonesel);
                request.setType(typesel);
                request.setDescription(descsel);
                request.setIndustry(industrysel);
                updateData(request);
                //Toast.makeText(getActivity(),namesel+"\n"+phonesel+"\n"+descsel+"\n"+typesel+"\n"+industrysel,Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,accountFragment);
                fragmentTransaction.addToBackStack(null);

                deleteData();
            }
        });


        return v;
    }

    private void deleteData(){
        Call<ApiStatus> call= ApiClient.getUserService().deleteAccount(accid);
        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),"Deleted successfully!",Toast.LENGTH_LONG).show();
                    fragmentTransaction.commit();
                }
                else {
                    Toast.makeText(getActivity(),"Cannot delete "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateData(AccountRequest request){
        Call<ApiStatus> call= ApiClient.getUserService().updateAccount(accid,request);
        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),"Updated successfully!",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(),"Cannot update "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getData(){
        Call<AccountRequest> call = ApiClient.getUserService().accountDetails(accid);
        call.enqueue(new Callback<AccountRequest>() {
            @Override
            public void onResponse(Call<AccountRequest> call, Response<AccountRequest> response) {
                if(response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        name.setText(jsonObject.getString("name"));
                        if(jsonObject.isNull("phone"))
                            phone.setText("");
                        else
                            phone.setText(jsonObject.getString("phone"));
                        if(jsonObject.isNull("description"))
                            desc.setText("");
                        else
                            desc.setText(jsonObject.getString("description"));
                        int typepos = typeAdapter.getPosition(jsonObject.getString("type"));
                        System.out.println(typepos);
                        type.setSelection(typepos);
                        int industrypos = industryAdapter.getPosition(jsonObject.getString("industry"));
                        System.out.println(industrypos);
                        industry.setSelection(industrypos);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getActivity(),"Unable to fetch",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<AccountRequest> call, Throwable t) {
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
