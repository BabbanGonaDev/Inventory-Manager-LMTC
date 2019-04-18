package com.bgenterprise.bglmtcinventory;

/**
Recycler adapter for the view all receipts activity.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AllReceiptsAdapter extends RecyclerView.Adapter<AllReceiptsAdapter.AllReceiptsViewHolder> implements Filterable {

    private final OnItemClickListener listener;
    private List<Receipts> allReceiptList;
    private List<Receipts> mFilteredList;
    Context context;

    public AllReceiptsAdapter(Context context, List<Receipts> allReceiptList, OnItemClickListener listener){
        this.allReceiptList = allReceiptList;
        this.mFilteredList = allReceiptList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AllReceiptsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View AllReceiptView = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipts_row, parent, false);

        return new AllReceiptsViewHolder(AllReceiptView);
    }

    @Override
    public void onBindViewHolder(final AllReceiptsViewHolder holder, final int position){
        final Receipts receipts = allReceiptList.get(position);

        holder.tv_lmd_name.setText("LMD Name: " + mFilteredList.get(position).getLmdName());
        holder.tv_lmd_id.setText("LMD ID: " + mFilteredList.get(position).getLmdID());
        holder.tv_no_of_receipts.setText("Number Of Receipts: " + mFilteredList.get(position).getReceiptCount());

    }

    public interface OnItemClickListener{
        void onClick(Receipts allReceiptList);
    }


    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }


    @Override
    public Filter getFilter(){

        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence charSequence){

                String charString = charSequence.toString();

                if(charString.isEmpty()){

                    mFilteredList = allReceiptList;
                }else{

                    List<Receipts> filteredList = new ArrayList<>();

                    for(Receipts receipts : allReceiptList){

                        if(receipts.getLmdName().toLowerCase().contains(charString) || receipts.getLmdID().toLowerCase().contains(charString)){

                            filteredList.add(receipts);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults){

                mFilteredList = (List<Receipts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class AllReceiptsViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_lmd_name;
        public TextView tv_lmd_id;
        public TextView tv_no_of_receipts;

        public AllReceiptsViewHolder(View view){
            super(view);
            tv_lmd_name = view.findViewById(R.id.tv_lmd_name);
            tv_lmd_id = view.findViewById(R.id.tv_lmd_id);
            tv_no_of_receipts = view.findViewById(R.id.tv_no_of_receipts);

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    listener.onClick(allReceiptList.get(getLayoutPosition()));
                }
            });

        }
    }

}


