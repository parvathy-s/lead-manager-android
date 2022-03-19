package com.example.leadmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityAdd extends Fragment implements AdapterView.OnItemSelectedListener{
    Button save;
    Spinner account, contact, stage;
    EditText name,amt, close;
    String isLoggedIn;
    OpportunityFragment opportunityFragment;
    FragmentTransaction fragmentTransaction;
    String namesel, amtsel, closesel, contactsel, stagesel, acsel;
    List<String> aname,aid, cname, cid;
    final Calendar myCalendar= Calendar.getInstance();
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.opportunity_add,container,false);
        name = v.findViewById(R.id.op_name);
        amt = v.findViewById(R.id.op_amt);
        close = v.findViewById(R.id.op_close);
        account = v.findViewById(R.id.op_acc);
        contact = v.findViewById(R.id.op_con);
        stage = v.findViewById(R.id.op_stage);
        save = v.findViewById(R.id.opsave);
        builder = new AlertDialog.Builder(getContext());

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        opportunityFragment =OpportunityFragment.newInstance(isLoggedIn);

        ArrayAdapter<CharSequence> stageAdapter = ArrayAdapter.createFromResource(getContext(),R.array.stage, android.R.layout.simple_spinner_item);
        stageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stage.setAdapter(stageAdapter);
        stage.setOnItemSelectedListener(this);

        setAccount();
        account.setOnItemSelectedListener(this);
        contact.setOnItemSelectedListener(this);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namesel = name.getText().toString();
                amtsel = amt.getText().toString();
                closesel = close.getText().toString();

                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,opportunityFragment);
                //fragmentTransaction.addToBackStack(null);

                if(namesel.length() == 0 || amtsel.length() == 0 || closesel.length() == 0){
                    Toast.makeText(getActivity(),"Enter all values",Toast.LENGTH_LONG).show();
                }
                else if(contactsel.equals("")){
                    Toast.makeText(getActivity(),"Create an associated contact",Toast.LENGTH_LONG).show();
                }
                else{
                    OpportunityRequest request= new OpportunityRequest();
                    request.setName(namesel);
                    request.setAmount(amtsel);
                    request.setClosedate(closesel);
                    request.setAccountid(acsel);
                    request.setStage(stagesel);
                    request.setContactid(contactsel);
                    saveData(request);
                }
            }
        });
        return v;
    }

    private void saveData(OpportunityRequest request){
        Call<ApiStatus> call= ApiClient.getUserService().createOpportunity(request);
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
                    alert.setTitle("Create Opportuniity");
                    alert.show();
                }
                else
                {
                    Toast.makeText(getActivity(),response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        close.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setAccount(){
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
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, aname);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    account.setAdapter(dataAdapter);
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

    private void setContact(){
        cname = new ArrayList<String>();
        cid = new ArrayList<String>();
        Call<List<ContactAccount>> listCall= ApiClient.getUserService().contactList(acsel);
        listCall.enqueue(new Callback<List<ContactAccount>>() {
            @Override
            public void onResponse(Call<List<ContactAccount>> call, Response<List<ContactAccount>> response) {
                if(response.isSuccessful()){
                    List<ContactAccount> contactList = response.body();
                    if(contactList.size() == 0){
                        Toast.makeText(getActivity(),"No associated contacts with this account",Toast.LENGTH_LONG).show();
                        contactsel="";
                    }
                        for(ContactAccount contact: contactList){
                            cname.add(contact.getName());
                            cid.add(contact.getSfid());
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, cname);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        contact.setAdapter(dataAdapter);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.op_stage:
                stagesel=parent.getItemAtPosition(position).toString();
                System.out.println("Selected :"+stagesel);
                break;
            case R.id.op_acc:
                acsel =aid.get(position);
                System.out.println("Selected :"+acsel);
                setContact();
                break;
            case R.id.op_con:
                contactsel =cid.get(position);
                System.out.println("Selected :"+contactsel);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
