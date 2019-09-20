package com.bgenterprise.bglmtcinventory;
/**
 * Recycler Adapter class for viewing the invoices for a specific LMD.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LMDInvoicesAdapter extends RecyclerView.Adapter<LMDInvoicesAdapter.LMDInvoicesViewHolder> {

    private List<Invoices> lmdInvoiceList;
    private final OnItemClickListener listener;
    Context context;

    public LMDInvoicesAdapter(Context context, List<Invoices> lmdInvoiceList, OnItemClickListener listener){
        this.listener = listener;
        this.lmdInvoiceList = lmdInvoiceList;
        this.context = context;
    }

    @Override
    public LMDInvoicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View lmdInvoicesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lmd_invoices_row, parent, false);

        return new LMDInvoicesViewHolder(lmdInvoicesView);
    }

    public interface OnItemClickListener{
        void onClick(Invoices invoices);
    }

    public class LMDInvoicesViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_invoice_itemID;
        public TextView tv_invoice_count;
        public TextView tv_invoice_price;
        public TextView tv_invoice_qty;
        public TextView tv_invoice_value;

        public LMDInvoicesViewHolder(View view){
            super(view);
            tv_invoice_itemID = view.findViewById(R.id.tv_invoice_itemID);
            tv_invoice_count = view.findViewById(R.id.tv_invoice_count);
            tv_invoice_price = view.findViewById(R.id.tv_invoice_price);
            tv_invoice_qty = view.findViewById(R.id.tv_invoice_qty);
            tv_invoice_value = view.findViewById(R.id.tv_invoice_value);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(lmdInvoiceList.get(getLayoutPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return lmdInvoiceList.size();
    }

    @Override
    public void onBindViewHolder(final LMDInvoicesViewHolder holder, final int position){
        final Invoices invoices = lmdInvoiceList.get(position);

        holder.tv_invoice_itemID.setText("Item ID: " + invoices.getItemID());
        holder.tv_invoice_count.setText("Count: " + invoices.getFODPhysicalCount());
        holder.tv_invoice_price.setText("Unit Price: " + invoices.getUnitPrice());
        holder.tv_invoice_qty.setText("Invoice Qty: " + invoices.getInvoiceQty());
        holder.tv_invoice_value.setText("Invoice Value: " + invoices.getInvoiceValue());

    }
}

