package com.example.leadmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private ArrayList<AccountItem> accountItems;
    public static class AccountViewHolder extends RecyclerView.ViewHolder{

        public TextView acc_name;
        public TextView acc_id;
        public TextView acc_type;
        public TextView acc_industry;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            acc_name = itemView.findViewById(R.id.acc_name);
            acc_id = itemView.findViewById(R.id.acc_id);
            acc_type = itemView.findViewById(R.id.acc_type);
            acc_industry = itemView.findViewById(R.id.acc_industry);
        }
    }

    public AccountAdapter(ArrayList<AccountItem> accountItems){
        this.accountItems = accountItems;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item, parent, false);
        AccountViewHolder avh = new AccountViewHolder(v);
        return avh;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountItem currentItem = accountItems.get(position);
        holder.acc_name.setText(currentItem.getName());
        holder.acc_id.setText(currentItem.getId());
        holder.acc_type.setText(currentItem.getType());
        holder.acc_industry.setText(currentItem.getIndustry());
    }

    @Override
    public int getItemCount() {
        return accountItems.size();
    }
}
