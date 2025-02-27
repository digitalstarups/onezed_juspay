package com.onezed.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Model.Operator;
import com.onezed.R;

import java.util.List;


public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.OperatorViewHolder> {

    private List<Operator> operatorList;
    private OnOperatorClickListener listener;

    public OperatorAdapter(List<Operator> operatorList, OnOperatorClickListener listener) {
        this.operatorList = operatorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OperatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operator, parent, false);
        return new OperatorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperatorViewHolder holder, int position) {
        Operator operator = operatorList.get(position);
        holder.operatorName.setText(operator.getName());

        // Set up click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOperatorClick(operator.getName(),operator.getCode());  // Pass the operator name to the fragment
            }
        });
    }

    @Override
    public int getItemCount() {
        return operatorList.size();
    }

    public class OperatorViewHolder extends RecyclerView.ViewHolder {
        TextView operatorName;

        public OperatorViewHolder(@NonNull View itemView) {
            super(itemView);
            operatorName = itemView.findViewById(R.id.operator_name);
        }
    }

    // Interface for click events
    public interface OnOperatorClickListener {
        void onOperatorClick(String operatorName, String code);
    }
}
