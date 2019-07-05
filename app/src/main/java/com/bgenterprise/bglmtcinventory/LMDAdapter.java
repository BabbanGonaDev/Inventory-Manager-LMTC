package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LMDAdapter extends RecyclerView.Adapter<LMDAdapter.LMDViewHolder> {

    private List<LMD> lmdList;
    private List<LMD> mFilteredList;
    private OnItemClickListener listener;
    Context context;

    public LMDAdapter(Context context, List<LMD> lmdList, OnItemClickListener listener){

        this.listener = listener;
        this.lmdList = lmdList;
        this.mFilteredList = lmdList;
        this.context = context;
    }

    @Override
    public LMDViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View lmdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lmd_row, parent, false);
        return new LMDViewHolder(lmdView);
    }

    public interface OnItemClickListener{
        void onClick(LMD lmd);
    }

    public class LMDViewHolder extends RecyclerView.ViewHolder{

        public TextView tvLMDID;
        public TextView tvLMDName;

        public LMDViewHolder(View view){
            super(view);
            tvLMDID = view.findViewById(R.id.tvLMDID);
            tvLMDName = view.findViewById(R.id.tvLMDName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mFilteredList.get(getLayoutPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return mFilteredList.size();
    }

    @Override
    public void onBindViewHolder(final LMDViewHolder holder, final int position){
        final LMD lmd = lmdList.get(position);

        holder.tvLMDID.setText("LMD ID: " + mFilteredList.get(position).getLMDID());
        holder.tvLMDName.setText("LMD Name: " + mFilteredList.get(position).getLMDName());
    }


    public Filter getFilter(){

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if(charString.isEmpty()){
                    mFilteredList = lmdList;
                }else{

                    List<LMD> filteredList = new ArrayList<>();

                    for(LMD lmd : lmdList){

                        if(lmd.getLMDName().toLowerCase().contains(charString)){
                            filteredList.add(lmd);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                mFilteredList = (List<LMD>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
