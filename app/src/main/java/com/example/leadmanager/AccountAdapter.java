package com.example.leadmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> implements Filterable {

    private ArrayList<AccountItem> accountItems;
    private ArrayList<AccountItem> accountListFull;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder{

        public TextView acc_name;
        public TextView acc_id;
        public TextView acc_type;
        public TextView acc_industry;

        public AccountViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            acc_name = itemView.findViewById(R.id.acc_name);
            acc_id = itemView.findViewById(R.id.acc_id);
            acc_type = itemView.findViewById(R.id.acc_type);
            acc_industry = itemView.findViewById(R.id.acc_industry);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public AccountAdapter(ArrayList<AccountItem> accountItems){

        this.accountItems = accountItems;
        accountListFull = new ArrayList<>(accountItems);
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item, parent, false);
        AccountViewHolder avh = new AccountViewHolder(v,mlistener);
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

    @Override
    public Filter getFilter() {
        return accountFilter;
    }

    private Filter accountFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<AccountItem> filteredList= new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(accountListFull);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for ( AccountItem item: accountListFull){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results= new FilterResults();
            results.values= filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            accountItems.clear();
            accountItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
