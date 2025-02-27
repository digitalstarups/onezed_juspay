package com.onezed.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Adapter.DynamicTabAdapter;
import com.onezed.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Plan {
    private String planId;
    private String description;
    private int amountInRupees;

    public Plan(String planId, String description, int amountInRupees) {
        this.planId = planId;
        this.description = description;
        this.amountInRupees = amountInRupees;
    }

    public String getPlanId() {
        return planId;
    }

    public String getDescription() {
        return description;
    }

    public int getAmountInRupees() {
        return amountInRupees;
    }
}

//public class Plan {
//    private final String name;
//    private final String value;
//
//    public Plan(String name, String value) {
//        this.name = name;
//        this.value = value;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getValue() {
//        return value;
//    }
//}

//public class Plan extends RecyclerView.Adapter<Plan.PlanViewHolder> {
//    private ArrayList<HashMap<String, String>> planList;
//    private DynamicTabAdapter.OnPlanClickListener onPlanClickListener;
//
//    public Plan(ArrayList<HashMap<String, String>> planList, DynamicTabAdapter.OnPlanClickListener onPlanClickListener) {
//        this.planList = planList;
//        this.onPlanClickListener = onPlanClickListener;
//    }
//
//    @NonNull
//    @Override
//    public Plan.PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
//        return new Plan.PlanViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull Plan.PlanViewHolder holder, int position) {
//        HashMap<String, String> plan = planList.get(position);
//        holder.txtName.setText("\u20B9 " + plan.get("Rs"));
//        holder.txtValue.setText(plan.get("Value"));
//        holder.txtAmount.setText(plan.get("validity"));
//
//        // Set click listener on the itemView
//        holder.itemView.setOnClickListener(v -> {
//            if (onPlanClickListener != null) {
//                onPlanClickListener.onPlanClick(plan.get("Rs"), plan.get("desc"), plan.get("validity"));
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return planList.size();
//    }
//
//    public static class PlanViewHolder extends RecyclerView.ViewHolder {
//        TextView txtName,txtValue, txtAmount ;
//
//        public PlanViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtName = itemView.findViewById(R.id.txtRs);
//            txtValue = itemView.findViewById(R.id.txtDesc);
//            txtAmount = itemView.findViewById(R.id.txtValidity);
//        }
//    }
//}