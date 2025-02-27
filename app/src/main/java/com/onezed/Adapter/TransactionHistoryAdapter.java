package com.onezed.Adapter;

import android.app.ListActivity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Model.TransactionHistoryModel;
import com.onezed.R;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private List<TransactionHistoryModel> transactionList;
    private OnTransactionClickListener listener;

    // Constructor
    public TransactionHistoryAdapter(List<TransactionHistoryModel> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

//    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
//
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            Toast.makeText(recyclerView.getContext(), "on Move", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//            // Toast.makeText(RecyclerView.get, "on Swiped ", Toast.LENGTH_SHORT).show();
//            //Remove swiped item from list and notify the RecyclerView
//            int position = viewHolder.getAdapterPosition();
//            transactionList.remove(position);
//            adapter.notifyDataSetChanged();
//
//        }
//    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_transaction_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionHistoryModel transaction = transactionList.get(position);

        // Bind data to the views
        holder.textViewTitle.setText(transaction.getStatus());
        holder.textViewAmount.setText("\u20B9 " + transaction.getAmount());
        holder.textTxnId.setText(transaction.getTransactionId());
        holder.textDate.setText(transaction.getDate());

        // Set colors based on status
        if (transaction.getStatus().equals("Suceesfull")||transaction.getStatus().equals("Successful")||transaction.getStatus().equals("SUCCESSFUL")||transaction.getStatus().equals("successful")||transaction.getStatus().equals("Completed")||transaction.getStatus().equals("true")){
            holder.textViewTitle.setTextColor(Color.parseColor("#157103"));
            holder.textViewAmount.setTextColor(Color.parseColor("#157103"));
            holder.frameLayout.setBackgroundColor(Color.parseColor("#157103"));
            holder.textViewTitle.setText("SUCCESSFUL");
        }else if(transaction.getStatus().equals("Pending")||transaction.getStatus().equals("pending")||transaction.getStatus().equals("PENDING")){
            holder.textViewTitle.setTextColor(Color.parseColor("#F7CB73"));
            holder.textViewAmount.setTextColor(Color.parseColor("#F7CB73"));
            holder.frameLayout.setBackgroundColor(Color.parseColor("#F7CB73"));
            holder.textViewTitle.setText("PENDING");
        }else {
            holder.textViewTitle.setTextColor(Color.parseColor("#e0240b"));
            holder.textViewAmount.setTextColor(Color.parseColor("#e0240b"));
            holder.frameLayout.setBackgroundColor(Color.parseColor("#e0240b"));
            holder.textViewTitle.setText("FAILED");
        }

        // Set an OnClickListener on the entire item view
        holder.itemView.setOnClickListener(v -> {
            // Notify the fragment about the click event
            listener.onTransactionClick(transaction);
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAmount, textTxnId, textDate;
        FrameLayout frameLayout;
        ImageView documentImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.txt_title);
            textViewAmount = itemView.findViewById(R.id.txt_amount);
            textTxnId = itemView.findViewById(R.id.txt_txn_id);
            textDate = itemView.findViewById(R.id.txt_date);
            frameLayout = itemView.findViewById(R.id.frame_layout);
            documentImg = itemView.findViewById(R.id.document_img);
        }
    }

    // Define the listener interface
    public interface OnTransactionClickListener {
        void onTransactionClick(TransactionHistoryModel transactionId);
    }
}

