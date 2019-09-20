package com.bgenterprise.bglmtcinventory;

/**
 *
 * The Recycler adapter class for viewing the Tellers in the database.
 *
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TellersAdapter extends RecyclerView.Adapter<TellersAdapter.TellersViewHolder> {

    private final OnItemClickListener listener;
    private List<Tellers> tellersList;
    private List<Tellers> mFilteredList;
    Context context;

    public TellersAdapter(Context context, List<Tellers> tellersList, OnItemClickListener listener){
        this.tellersList = tellersList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public TellersViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View tellerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tellers_row, parent, false);

        return new TellersViewHolder(tellerView);
    }

    @Override
    public int getItemCount(){ return tellersList.size(); }

    @Override
    public void onBindViewHolder(final TellersViewHolder holder, final int position){
        final Tellers tellers = tellersList.get(position);

        TellerDBHandler tellerDBHandler = new TellerDBHandler(context);
        try {
            Integer current_amount = tellerDBHandler.getTotalReceiptAmount(tellers.getTeller_id());
            if (Integer.valueOf(current_amount) < Integer.valueOf(tellers.getTeller_amount())) {
                holder.tv_tellerID.setBackgroundColor(Color.RED);
                holder.tv_tellerID.setTextColor(Color.WHITE);
            } else {
                holder.tv_tellerID.setBackgroundColor(Color.GREEN);
                holder.tv_tellerID.setTextColor(Color.BLUE);
            }

            holder.tv_tellerID.setText("Teller ID: " + tellers.getTeller_id());
            holder.tv_tellerAmount.setText("Teller Amount: " + tellers.getTeller_amount());
            holder.tv_tellerBank.setText("Bank: " + tellers.getTeller_bank());
            holder.tv_receiptCount.setText("Total Number of Receipts: " + tellers.getReceipt_count());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public interface OnItemClickListener{
        void onClick(Tellers tellers);
    }

//    @Override
//    public Filter getFilter(){
//
//        return new Filter(){
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence){
//
//                String charString = charSequence.toString();
//
//                if(charString.isEmpty()){
//
//                    mFilteredList = tellersList;
//                }else{
//
//                    List<Tellers> filteredList = new ArrayList<>();
//
//                    for(Tellers tellers : tellersList){
//
//                        if(tellers.getTeller_id().toLowerCase().contains(charString) || tellers.getTeller_bank().toLowerCase().contains(charString)){
//
//                            filteredList.add(tellers);
//                        }
//                    }
//                    mFilteredList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mFilteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults){
//
//                mFilteredList = (List<Tellers>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

    public class TellersViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_tellerID;
        public TextView tv_tellerBank;
        public TextView tv_tellerAmount;
        public TextView tv_receiptCount;

        public TellersViewHolder(View view){
            super(view);
            tv_tellerID = view.findViewById(R.id.tv_tellerID);
            tv_tellerBank = view.findViewById(R.id.tv_tellerBank);
            tv_tellerAmount = view.findViewById(R.id.tv_tellerAmount);
            tv_receiptCount = view.findViewById(R.id.tv_receiptCount);

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    listener.onClick(tellersList.get(getLayoutPosition()));
                }
            });
        }
    }

}
