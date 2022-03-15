package com.example.leadmanager;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadConvert extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String ARG_ID="id";
    Button convert, update;
    int flag =0;
    Spinner type, industry, stage;
    EditText aname, fname, lname, title, oname, closedate;
    String isLoggedIn;
    LeadFragment leadFragment;
    ArrayAdapter<CharSequence> typeAdapter, industryAdapter, stageAdapter;
    FragmentTransaction fragmentTransaction;
    String anamesel,fnamesel,lnamesel,namesel,titlesel,onamesel,closesel,typesel,industrysel,stagesel, lextid;
    final Calendar myCalendar= Calendar.getInstance();

    public static LeadConvert newInstance(String lid){
        LeadConvert leadConvert= new LeadConvert();
        Bundle args= new Bundle();
        args.putString(ARG_ID,lid);
        leadConvert.setArguments(args);
        System.out.println(lid);
        return leadConvert;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lead_convert,container,false);

        convert = v.findViewById(R.id.leadcon);
        update = v.findViewById(R.id.leadup);
        type = v.findViewById(R.id.leadcon_type);
        industry = v.findViewById(R.id.leadcon_industry);
        stage = v.findViewById(R.id.leadcon_stage);
        aname = v.findViewById(R.id.leadcon_acc);
        fname = v.findViewById(R.id.leadcon_fname);
        lname = v.findViewById(R.id.leadcon_lname);
        title = v.findViewById(R.id.leadcon_title);
        oname = v.findViewById(R.id.leadcon_op);
        closedate = v.findViewById(R.id.leadcon_close);

        if(getArguments() != null) {
            lextid = getArguments().getString(ARG_ID);
        }

        typeAdapter = ArrayAdapter.createFromResource(getContext(),R.array.account_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
        type.setOnItemSelectedListener(this);

        industryAdapter = ArrayAdapter.createFromResource(getContext(),R.array.account_industry, android.R.layout.simple_spinner_item);
        industryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        industry.setAdapter(industryAdapter);
        industry.setOnItemSelectedListener(this);

        stageAdapter = ArrayAdapter.createFromResource(getContext(),R.array.stage, android.R.layout.simple_spinner_item);
        stageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stage.setAdapter(stageAdapter);
        stage.setOnItemSelectedListener(this);

        SessionManagement sessionManagement = new SessionManagement(getContext());
        isLoggedIn = sessionManagement.getSession();
        leadFragment = LeadFragment.newInstance(isLoggedIn);

        fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,leadFragment);
        fragmentTransaction.addToBackStack(null);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        closedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        getData();

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 1 ){
                    Toast.makeText(getActivity(),"Already created",Toast.LENGTH_LONG).show();
                }
                else {
                    fnamesel = fname.getText().toString();
                    lnamesel = lname.getText().toString();
                    namesel = fnamesel + " " + lnamesel;
                    anamesel = aname.getText().toString();
                    onamesel = oname.getText().toString();
                    titlesel = title.getText().toString();
                    closesel = closedate.getText().toString();

                    if (namesel.length() == 0 || anamesel.length() == 0 || onamesel.length() == 0 || titlesel.length() == 0 || closesel.length() == 0)
                        Toast.makeText(getActivity(), "Enter all values", Toast.LENGTH_LONG).show();
                    else {
                        LeadConvertRequest request = new LeadConvertRequest();
                        request.setAname(anamesel);
                        request.setAtype(typesel);
                        request.setAindustry(industrysel);
                        request.setCfname(fnamesel);
                        request.setClname(lnamesel);
                        request.setCname(namesel);
                        request.setCtitle(titlesel);
                        request.setOname(onamesel);
                        request.setOclosedate(closesel);
                        request.setOstagename(stagesel);
                        convert(request);
                    }
                }
            }
        });

        update.setVisibility(View.GONE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0)
                    Toast.makeText(getActivity(),"Create objects first",Toast.LENGTH_LONG).show();
                else
                    updateConvert();
            }
        });
        return v;
    }

    private void convert(LeadConvertRequest request){

       Call<ApiStatus> call= ApiClient.getUserService().convertLead(request);
        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),"Objects created successfully!",Toast.LENGTH_LONG).show();
                    flag = 1;
                    update.setVisibility(View.VISIBLE);
                    convert.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(getActivity(),"Cannot insert "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateConvert(){
        Call<ApiStatus> call= ApiClient.getUserService().updateConverted(anamesel,namesel,onamesel,lextid);

        call.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),"Converted successfully!",Toast.LENGTH_LONG).show();
                    fragmentTransaction.commit();
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
                        aname.setText(jsonObject.getString("company"));
                        title.setText(jsonObject.getString("title"));

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

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        closedate.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.leadcon_type:
                typesel = parent.getItemAtPosition(position).toString();
                break;
            case R.id.leadcon_industry:
                industrysel = parent.getItemAtPosition(position).toString();
                break;
            case R.id.leadcon_stage:
                stagesel = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
