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

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.OpportunityViewHolder> implements Filterable {

    private ArrayList<OpportunityResponse> opportunityItems;
    private ArrayList<OpportunityResponse> opportunityListFull;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class OpportunityViewHolder extends RecyclerView.ViewHolder{

        public TextView op_name;
        public TextView op_id;
        public TextView op_stage;
        public TextView op_ac;

        public OpportunityViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            op_name = itemView.findViewById(R.id.op_name);
            op_id = itemView.findViewById(R.id.op_id);
            op_stage = itemView.findViewById(R.id.op_stage);
            op_ac = itemView.findViewById(R.id.op_ac);

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

    public OpportunityAdapter(ArrayList<OpportunityResponse> opportunityItems){
        this.opportunityItems = opportunityItems;
        opportunityListFull = new ArrayList<>(opportunityItems);
    }

    @NonNull
    @Override
    public OpportunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opportunity_item, parent, false);
        OpportunityViewHolder ovh = new OpportunityViewHolder(v,mlistener);
        return ovh;
    }

    @Override
    public void onBindViewHolder(@NonNull OpportunityViewHolder holder, int position) {
        OpportunityResponse currentItem = opportunityItems.get(position);
        holder.op_name.setText(currentItem.getOname());
        holder.op_id.setText(currentItem.getO_extid__c());
        holder.op_stage.setText(currentItem.getStagename());
        holder.op_ac.setText(currentItem.getAname());
    }

    @Override
    public int getItemCount() {
        return opportunityItems.size();
    }

    @Override
    public Filter getFilter() {
        return opportunityFilter;
    }

    private Filter opportunityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<OpportunityResponse> filteredList= new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(opportunityListFull);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for ( OpportunityResponse item: opportunityListFull){
                    if(item.getOname().toLowerCase().contains(filterPattern)){
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
            opportunityItems.clear();
            opportunityItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
