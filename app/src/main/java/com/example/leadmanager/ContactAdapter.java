package com.example.leadmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<ContactResponse> contactItems;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView con_name;
        public TextView con_id;
        public TextView con_title;
        public TextView acc_name;

        public ContactViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            acc_name = itemView.findViewById(R.id.con_acc_name);
            con_id = itemView.findViewById(R.id.con_id);
            con_title = itemView.findViewById(R.id.con_title);
            con_name = itemView.findViewById(R.id.con_name);

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

    public ContactAdapter(ArrayList<ContactResponse> contactItems){
        this.contactItems = contactItems;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ContactViewHolder cvh = new ContactViewHolder(v,mlistener);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactResponse currentItem = contactItems.get(position);
        holder.con_name.setText(currentItem.getCname());
        holder.con_id.setText(currentItem.getC_extd__c());
        holder.con_title.setText(currentItem.getTitle());
        holder.acc_name.setText(currentItem.getAname());
    }

    @Override
    public int getItemCount() {
        return contactItems.size();
    }
}

