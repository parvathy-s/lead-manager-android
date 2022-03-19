package com.example.leadmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class LeadAdd extends Fragment implements AdapterView.OnItemSelectedListener{
    Button save;
    Spinner status;
    EditText fname, lname, company, email, title;
    String isLoggedIn;
    LeadFragment leadFragment;
    FragmentTransaction fragmentTransaction;
    String fnamesel, lnamesel, namesel, companysel, emailsel, titlesel, statussel;
    private AlertDialog.Builder builder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lead_add,container,false);
        save = v.findViewById(R.id.leadsave);
        fname = v.findViewById(R.id.lead_fname);
        lname = v.findViewById(R.id.lead_lname);
        company = v.findViewById(R.id.lead_company);
        email = v.findViewById(R.id.lead_email);
        title = v.findViewById(R.id.lead_title);
        status = v.findViewById(R.id.lead_status);
        builder = new AlertDialog.Builder(getContext());

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(),R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(statusAdapter);
        status.setOnItemSelectedListener(this);

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        leadFragment = LeadFragment.newInstance(isLoggedIn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnamesel = fname.getText().toString();
                lnamesel = lname.getText().toString();
                namesel = fnamesel + " "+ lnamesel;
                companysel= company.getText().toString();
                emailsel= email.getText().toString();
                titlesel = title.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,leadFragment);
                //fragmentTransaction.addToBackStack(null);

                if(namesel.length() == 0 || companysel.length() == 0 || emailsel.length() == 0 || titlesel.length() == 0)
                    Toast.makeText(getActivity(), "Enter all values", Toast.LENGTH_SHORT).show();
                else if(statussel.equals("Closed - Converted")){
                    Toast.makeText(getActivity(),"Lead cannot be closed at the initial stage",Toast.LENGTH_LONG).show();
                }
                else{
                    if(emailsel.matches(emailPattern)){
                        LeadRequest request= new LeadRequest();
                        request.setFirstname(fnamesel);
                        request.setLastname(lnamesel);
                        request.setName(namesel);
                        request.setCompany(companysel);
                        request.setEmail(emailsel);
                        request.setTitle(titlesel);
                        request.setStatus(statussel);
                        saveData(request);
                    }
                    else
                        Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    private void saveData(LeadRequest request){
        Call<ApiStatus> call= ApiClient.getUserService().createLead(request);
        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    builder.setMessage("Salesforce is working in the background. Please wait for the changes to take effect")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fragmentTransaction.commit();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Create Lead");
                    alert.show();
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
        statussel = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
