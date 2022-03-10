package com.example.leadmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.OpportunityViewHolder> {

    private ArrayList<OpportunityResponse> opportunityItems;
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
}
