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
import java.util.Locale;

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.LeadViewHolder> implements Filterable {

    private ArrayList<LeadResponse> leadItems;
    private ArrayList<LeadResponse> leadListFull;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class LeadViewHolder extends RecyclerView.ViewHolder{

        public TextView lead_name;
        public TextView lead_id;
        public TextView lead_company;
        public TextView lead_status;

        public LeadViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            lead_name = itemView.findViewById(R.id.lead_name);
            lead_id = itemView.findViewById(R.id.lead_id);
            lead_company = itemView.findViewById(R.id.lead_company);
            lead_status = itemView.findViewById(R.id.lead_status);

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

    public LeadAdapter(ArrayList<LeadResponse> leadItems){
        this.leadItems = leadItems;
        leadListFull = new ArrayList<>(leadItems);
    }

    @NonNull
    @Override
    public LeadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_item, parent, false);
        LeadViewHolder lvh = new LeadViewHolder(v,mlistener);
        return lvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LeadViewHolder holder, int position) {
        LeadResponse currentItem = leadItems.get(position);
        holder.lead_name.setText(currentItem.getName());
        holder.lead_id.setText(currentItem.getL_extid__c());
        holder.lead_company.setText(currentItem.getCompany());
        holder.lead_status.setText(currentItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return leadItems.size();
    }

    @Override
    public Filter getFilter() {
        return leadFilter;
    }

    private Filter leadFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<LeadResponse> filteredList= new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(leadListFull);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for ( LeadResponse item: leadListFull){
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

            leadItems.clear();
            leadItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
