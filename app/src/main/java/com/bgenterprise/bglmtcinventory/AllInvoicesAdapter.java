package com.bgenterprise.bglmtcinventory;

/**
    Recycler Adapter for the View all invoices page.
*/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllInvoicesAdapter extends RecyclerView.Adapter<AllInvoicesAdapter.AllInvoicesViewHolder> {

    private List<Invoices> invoicesList;
    private final OnItemClickListener listener;
    Context context;

    public AllInvoicesAdapter( Context context, List<Invoices> invoicesList, OnItemClickListener listener){
        this.invoicesList = invoicesList;
        this.context = context;
        this.listener = listener;

    }

    @Override
    public AllInvoicesViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View invoiceView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_invoice_row, parent, false);

        return new AllInvoicesViewHolder(invoiceView);
    }

    public interface OnItemClickListener{
        void onClick(Invoices invoicesList);
    }

    public class AllInvoicesViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_invoice_LMDID;
        public TextView tv_invoice_date;
        public TextView tv_invoiceCount;
        public TextView tv_invoiceAmount;

        public AllInvoicesViewHolder(View view){
            super(view);
            tv_invoice_LMDID = view.findViewById(R.id.tv_invoice_LMDID);
            tv_invoice_date = view.findViewById(R.id.tv_invoice_date);
            tv_invoiceCount = view.findViewById(R.id.tv_invoiceCount);
            tv_invoiceAmount = view.findViewById(R.id.tv_invoiceAmount);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(invoicesList.get(getLayoutPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        int size = 0;
        try {
            size = invoicesList.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return size;
    }


    @Override
    public void onBindViewHolder(final AllInvoicesViewHolder holder, final int position){
        final Invoices invoices = invoicesList.get(position);

        try {
            holder.tv_invoice_LMDID.setText("LMD ID: " + invoices.getLMDID());
            holder.tv_invoice_date.setText("Invoice Date: " + invoices.getTxnDate());
            holder.tv_invoiceCount.setText("No of Invoices: " + invoices.getInvoiceCount());
            holder.tv_invoiceAmount.setText("Total Invoice Amount: " + invoices.getInvoiceAmount());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
