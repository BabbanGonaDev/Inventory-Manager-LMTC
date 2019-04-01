package com.bgenterprise.bglmtcinventory;

/**
 * Recycler adapter for viewing all the receipts belonging to a specific LMD.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LMDReceiptsAdapter extends RecyclerView.Adapter<LMDReceiptsAdapter.LMDReceiptsViewHolder> {

    private List<Receipts> lmdReceiptList;
    Context context;

    public LMDReceiptsAdapter(List<Receipts> lmdReceiptList, Context context){
        this.lmdReceiptList = lmdReceiptList;
        this.context = context;
    }

    @Override
    public LMDReceiptsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View LmdReceiptView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lmd_receipts_row, parent, false);

        return new LMDReceiptsViewHolder(LmdReceiptView);
    }


    public class LMDReceiptsViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_receipt_id;
        public TextView tv_receipt_amount;
        public TextView tv_moneycollectedby;
        public TextView tv_receipt_date;
        public TextView tv_receipt_sync_status;

        public LMDReceiptsViewHolder(View view){
            super(view);
            tv_receipt_id = view.findViewById(R.id.tv_receipt_id);
            tv_receipt_amount = view.findViewById(R.id.tv_receipt_amount);
            tv_moneycollectedby = view.findViewById(R.id.tv_moneycollectedby);
            tv_receipt_date = view.findViewById(R.id.tv_receipt_date);
            tv_receipt_sync_status = view.findViewById(R.id.tv_receipt_sync_status);
        }
    }

    @Override
    public int getItemCount(){ return lmdReceiptList.size(); }

    @Override
    public void onBindViewHolder(final LMDReceiptsViewHolder holder, final int position){
        final Receipts receipts = lmdReceiptList.get(position);

        holder.tv_receipt_id.setText("Receipt ID: " + receipts.getReceiptID());
        holder.tv_receipt_amount.setText("Amount: " + receipts.getReceiptAmount());
        holder.tv_moneycollectedby.setText("Money Collected By: " + receipts.getMoneycollectedby());
        holder.tv_receipt_date.setText("Date: " + receipts.getReceiptDate());
        holder.tv_receipt_sync_status.setText("Sync Status: " + receipts.getReceiptSyncStatus());

    }
}
