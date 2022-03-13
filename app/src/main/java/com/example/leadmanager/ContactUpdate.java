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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUpdate extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String ARG_ID="id";
    Button update, delete;
    Spinner account;
    EditText fname, lname, title, phone, email;
    String isLoggedIn, cid;
    ContactFragment contactFragment;
    FragmentTransaction fragmentTransaction;
    String namesel, fnamesel, lnamesel, titlesel, phonesel, emailsel, acsel;
    List<String> aname;
    List<String> aid;
    ArrayAdapter<String> dataAdapter;

    public static ContactUpdate newInstance(String cid){
        ContactUpdate contactUpdate= new ContactUpdate();
        Bundle args= new Bundle();
        args.putString(ARG_ID,cid);
        contactUpdate.setArguments(args);
        System.out.println(cid);
        return contactUpdate;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_update,container,false);
        fname = v.findViewById(R.id.fname_con);
        lname = v.findViewById(R.id.lname_con);
        title = v.findViewById(R.id.title_con);
        phone = v.findViewById(R.id.phone_con);
        email = v.findViewById(R.id.email_con);
        account = v.findViewById(R.id.acc_con);
        update = v.findViewById(R.id.conupdate);
        delete = v.findViewById(R.id.condelete);

        if(getArguments() != null) {
            cid = getArguments().getString(ARG_ID);
        }

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        contactFragment=ContactFragment.newInstance(isLoggedIn);

        account.setOnItemSelectedListener(this);

        setSpinner();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnamesel = fname.getText().toString();
                lnamesel = lname.getText().toString();
                namesel = fnamesel + " " + lnamesel;
                titlesel = title.getText().toString();
                phonesel = phone.getText().toString();
                emailsel = email.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (namesel.length() == 0 || titlesel.length() == 0 || phonesel.length() == 0 || emailsel.length() == 0){
                    Toast.makeText(getActivity(),"Enter all values", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(emailsel.matches(emailPattern)){

                        ContactRequest request= new ContactRequest();
                        request.setFirstname(fnamesel);
                        request.setLastname(lnamesel);
                        request.setName(namesel);
                        request.setAccountid(acsel);
                        request.setTitle(titlesel);
                        request.setPhone(phonesel);
                        request.setEmail(emailsel);
                        update(request);
                    }
                    else
                        Toast.makeText(getActivity(),"Invalid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,contactFragment);
                fragmentTransaction.addToBackStack(null);
                deleteData();
            }
        });
        return v;
    }

    private void deleteData(){
        Call<ApiStatus> call = ApiClient.getUserService().deleteContact(cid);
        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),"Deleted successfully!",Toast.LENGTH_LONG).show();
                    fragmentTransaction.commit();
                }
                else{
                    Toast.makeText(getActivity(),"Cannot delete "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void update(ContactRequest request){
        Call<ApiStatus> call= ApiClient.getUserService().updateContact(cid,request);
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

    private void setSpinner(){
        aname = new ArrayList<String>();
        aid = new ArrayList<String>();

        Call<List<ContactAccount>> listCall = ApiClient.getUserService().accountList(isLoggedIn);
        listCall.enqueue(new Callback<List<ContactAccount>>() {
            @Override
            public void onResponse(Call<List<ContactAccount>> call, Response<List<ContactAccount>> response) {
                if(response.isSuccessful()){
                    List<ContactAccount> accountList = response.body();
                    for(ContactAccount account: accountList){
                        aname.add(account.getName());
                        aid.add(account.getSfid());
                    }
                    dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, aname);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    account.setAdapter(dataAdapter);
                    getData();
                }
                else{
                    Toast.makeText(getActivity(),response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ContactAccount>> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getData(){
        Call<ContactRequest> call= ApiClient.getUserService().contactDetails(cid);
        call.enqueue(new Callback<ContactRequest>() {
            @Override
            public void onResponse(Call<ContactRequest> call, Response<ContactRequest> response) {
                if(response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        fname.setText(jsonObject.getString("firstname"));
                        lname.setText(jsonObject.getString("lastname"));
                        title.setText(jsonObject.getString("title"));
                        if(jsonObject.isNull("phone"))
                            phone.setText("");
                        else
                            phone.setText(jsonObject.getString("phone"));
                        if(jsonObject.isNull("email"))
                            email.setText("");
                        else
                            email.setText(jsonObject.getString("email"));
                        String acid = jsonObject.getString("accountid");
                        int pos = aid.indexOf(acid);
                        System.out.println(pos);
                        account.setSelection(pos);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getActivity(),"Unable to fetch",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ContactRequest> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        acsel =aid.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
