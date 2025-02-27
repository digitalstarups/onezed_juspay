package com.onezed.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {
    private ArrayList<HashMap<String, String>> planList;
    private DynamicTabAdapter.OnPlanClickListener onPlanClickListener;

    public PlanAdapter(ArrayList<HashMap<String, String>> planList, DynamicTabAdapter.OnPlanClickListener onPlanClickListener) {
        this.planList = planList;
        this.onPlanClickListener = onPlanClickListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        HashMap<String, String> plan = planList.get(position);
        holder.txtRs.setText("\u20B9 " + plan.get("Rs"));
        holder.txtDesc.setText(plan.get("desc"));
        holder.txtValidity.setText(plan.get("validity"));

        // Set click listener on the itemView
        holder.itemView.setOnClickListener(v -> {
            if (onPlanClickListener != null) {
                onPlanClickListener.onPlanClick(plan.get("Rs"), plan.get("desc"), plan.get("validity"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtRs, txtDesc, txtValidity;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRs = itemView.findViewById(R.id.txtRs);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtValidity = itemView.findViewById(R.id.txtValidity);
        }
    }
}


