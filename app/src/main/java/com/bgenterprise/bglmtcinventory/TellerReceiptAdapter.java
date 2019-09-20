package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 *
 * The recycler adapter class for viewing the receipts under each teller selected.
 */

public class TellerReceiptAdapter extends RecyclerView.Adapter<TellerReceiptAdapter.TellerReceiptViewHolder> {

    private final OnItemClickListener listener;
    private List<Tellers> tellersList;
    Context context;

    public TellerReceiptAdapter(Context context, List<Tellers> tellersList, OnItemClickListener listener){
        this.tellersList = tellersList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public TellerReceiptViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View receiptView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teller_receipt_row, parent, false);

        return new TellerReceiptViewHolder(receiptView);
    }

    @Override
    public int getItemCount(){ return tellersList.size(); }

    @Override
    public void onBindViewHolder(final TellerReceiptViewHolder holder, final int position){
        final Tellers tellers = tellersList.get(position);

        holder.tv_teller_receipt_id.setText("Receipt ID: " + tellers.getReceipt_id());
        holder.tv_teller_receipt_amount.setText("Receipt Amount: " + tellers.getReceipt_amount());

    }

    public interface OnItemClickListener{
        void onClick(Tellers tellers);
    }

    public class TellerReceiptViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_teller_receipt_id;
        public TextView tv_teller_receipt_amount;

        public TellerReceiptViewHolder(View view){
            super(view);
            tv_teller_receipt_id = view.findViewById(R.id.tv_teller_receipt_id);
            tv_teller_receipt_amount = view.findViewById(R.id.tv_teller_receipt_amount);

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    listener.onClick(tellersList.get(getLayoutPosition()));
                }
            });
        }
    }


}
