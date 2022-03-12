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

public class LeadUpdate extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String ARG_ID="id";
    Button update,del;
    Spinner status;
    EditText fname, lname, company, email, title;
    String isLoggedIn;
    LeadFragment leadFragment;
    ArrayAdapter<CharSequence> statusAdapter;
    FragmentTransaction fragmentTransaction;
    String fnamesel, lnamesel, namesel, companysel, emailsel, titlesel, statussel, lextid;

    public static LeadUpdate newInstance(String lid){
        LeadUpdate leadUpdate= new LeadUpdate();
        Bundle args= new Bundle();
        args.putString(ARG_ID,lid);
        leadUpdate.setArguments(args);
        System.out.println(lid);
        return leadUpdate;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lead_update,container,false);
        update = v.findViewById(R.id.leadupdate);
        del = v.findViewById(R.id.leaddel);
        fname = v.findViewById(R.id.lead_fname);
        lname = v.findViewById(R.id.lead_lname);
        company = v.findViewById(R.id.lead_company);
        email = v.findViewById(R.id.lead_email);
        title = v.findViewById(R.id.lead_title);
        status = v.findViewById(R.id.lead_status);

        if(getArguments() != null) {
            lextid = getArguments().getString(ARG_ID);
        }

        statusAdapter = ArrayAdapter.createFromResource(getContext(),R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(statusAdapter);
        status.setOnItemSelectedListener(this);

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        leadFragment = LeadFragment.newInstance(isLoggedIn);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnamesel = fname.getText().toString();
                lnamesel = lname.getText().toString();
                namesel = fnamesel + " "+ lnamesel;
                companysel= company.getText().toString();
                emailsel= email.getText().toString();
                titlesel = title.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if(namesel.length() == 0 || companysel.length() == 0 || emailsel.length() == 0 || titlesel.length() == 0)
                    Toast.makeText(getActivity(), "Enter all values", Toast.LENGTH_SHORT).show();
                else {
                    if (emailsel.matches(emailPattern)) {
                        //Toast.makeText(getActivity(),namesel+"\n"+companysel+"\n"+emailsel+"\n"+titlesel+"\n"+statussel,Toast.LENGTH_LONG).show();
                        LeadRequest request = new LeadRequest();
                        request.setFirstname(fnamesel);
                        request.setLastname(lnamesel);
                        request.setName(namesel);
                        request.setCompany(companysel);
                        request.setEmail(emailsel);
                        request.setTitle(titlesel);
                        request.setStatus(statussel);
                        updateData(request);
                    } else
                        Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,leadFragment);
                fragmentTransaction.addToBackStack(null);
                deleteData();
            }
        });

        getData();

        return v;
    }

    private void deleteData(){
        Call<ApiStatus> call= ApiClient.getUserService().deleteLead(lextid);
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

    private void updateData(LeadRequest request){
        Call<ApiStatus> call= ApiClient.getUserService().updateLead(lextid,request);
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
        Call<LeadRequest> call= ApiClient.getUserService().leadDetails(lextid);
        call.enqueue(new Callback<LeadRequest>() {
            @Override
            public void onResponse(Call<LeadRequest> call, Response<LeadRequest> response) {
                if(response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        fname.setText(jsonObject.getString("firstname"));
                        lname.setText(jsonObject.getString("lastname"));
                        company.setText(jsonObject.getString("company"));
                        email.setText(jsonObject.getString("email"));
                        title.setText(jsonObject.getString("title"));
                        int statuspos = statusAdapter.getPosition(jsonObject.getString("status"));
                        System.out.println(statuspos);
                        status.setSelection(statuspos);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getActivity(),"Unable to fetch",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<LeadRequest> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        statussel = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
