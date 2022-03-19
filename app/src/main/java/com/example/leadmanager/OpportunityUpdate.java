package com.example.leadmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityUpdate extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String ARG_ID="id";
    int flag=0;
    Button update,del;
    Spinner account, contact, stage;
    EditText name,amt, close;
    String isLoggedIn;
    OpportunityFragment opportunityFragment;
    FragmentTransaction fragmentTransaction;
    String namesel, amtsel, closesel, contactsel, stagesel, acsel,oid;
    List<String> aname,aid, cname, cid;
    ArrayAdapter<CharSequence> stageAdapter;
    final Calendar myCalendar= Calendar.getInstance();
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;

    public static OpportunityUpdate newInstance(String oid){
        OpportunityUpdate opportunityUpdate=new OpportunityUpdate();
        Bundle args= new Bundle();
        args.putString(ARG_ID,oid);
        opportunityUpdate.setArguments(args);
        System.out.println(oid);
        return opportunityUpdate;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.opportunity_update,container,false);

        name = v.findViewById(R.id.op_name);
        amt = v.findViewById(R.id.op_amt);
        close = v.findViewById(R.id.op_close);
        account = v.findViewById(R.id.op_acc);
        contact = v.findViewById(R.id.op_con);
        stage = v.findViewById(R.id.op_stage);
        update = v.findViewById(R.id.opupdate);
        del = v.findViewById(R.id.opdel);
        builder = new AlertDialog.Builder(getContext());

        if(getArguments() != null) {
            oid = getArguments().getString(ARG_ID);
        }

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        opportunityFragment =OpportunityFragment.newInstance(isLoggedIn);

        stageAdapter = ArrayAdapter.createFromResource(getContext(),R.array.stage, android.R.layout.simple_spinner_item);
        stageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stage.setAdapter(stageAdapter);
        stage.setOnItemSelectedListener(this);

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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        setAccount();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namesel = name.getText().toString();
                amtsel = amt.getText().toString();
                closesel = close.getText().toString();

                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,opportunityFragment);
                fragmentTransaction.addToBackStack(null);

                if(namesel.length() == 0 || amtsel.length() == 0 || closesel.length() == 0){
                    Toast.makeText(getActivity(),"Enter all values",Toast.LENGTH_LONG).show();
                }
                else{
                    //Toast.makeText(getActivity(),namesel+"\n"+amtsel+"\n"+closesel+"\n"+stagesel+"\n"+acsel+"\n"+contactsel,Toast.LENGTH_LONG).show();
                    OpportunityRequest request= new OpportunityRequest();
                    request.setName(namesel);
                    request.setAmount(amtsel);
                    request.setClosedate(closesel);
                    request.setAccountid(acsel);
                    request.setStage(stagesel);
                    request.setContactid(contactsel);
                    updateData(request);
                }
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,opportunityFragment);
                //fragmentTransaction.addToBackStack(null);
                builder.setMessage("Are you sure you want to delete this record ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteData();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Delete Opportunity");
                alert.show();
            }
        });

        return v;
    }

    private void deleteData(){
        Call<ApiStatus> call = ApiClient.getUserService().deleteOpportunity(oid);
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

    private void updateData(OpportunityRequest request){
        Call<ApiStatus> call= ApiClient.getUserService().updateOpportunity(oid, request);
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
                    }
                    for(ContactAccount contact: contactList){
                        cname.add(contact.getName());
                        cid.add(contact.getSfid());
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, cname);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    contact.setAdapter(dataAdapter);

                    if(flag==0){
                        getData();
                        flag=1;
                    }
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
        Call<OpportunityInfo> call= ApiClient.getUserService().opportunityDetails(oid);
        call.enqueue(new Callback<OpportunityInfo>() {
            @Override
            public void onResponse(Call<OpportunityInfo> call, Response<OpportunityInfo> response) {
                if(response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        name.setText(jsonObject.getString("name"));
                        if(jsonObject.isNull("amount"))
                            amt.setText("");
                        else
                            amt.setText(jsonObject.getString("amount"));
                        close.setText(jsonObject.getString("closedate"));
                        String stageid= jsonObject.getString("stagename");
                        String acid = jsonObject.getString("accountid");
                        String conid = jsonObject.getString("contact__c");

                        int apos = aid.indexOf(acid);
                        account.setSelection(apos);

                        int spos = stageAdapter.getPosition(stageid);
                        stage.setSelection(spos);

                        int cpos = cid.indexOf(conid);
                        contact.setSelection(cpos);

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getActivity(),"Unable to fetch",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<OpportunityInfo> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
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
