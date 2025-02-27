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
import java.util.List;


public class RechargePlanAdapter extends RecyclerView.Adapter<RechargePlanAdapter.PlanViewHolder> {
    private ArrayList<HashMap<String, String>> planList;
    private RechargeTabAdapter.OnPlanClickListener onPlanClickListener;

    public RechargePlanAdapter(ArrayList<HashMap<String, String>> planList, RechargeTabAdapter.OnPlanClickListener onPlanClickListener) {
        this.planList = planList;
        this.onPlanClickListener = onPlanClickListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        HashMap<String, String> plan = planList.get(position);

        holder.txtRs.setText("\u20B9 " + plan.get("amountInRupees"));
        holder.txtDesc.setText("Validity : "+plan.get("Validity"));
        holder.txtValidity.setText("Data : "+plan.get("Data"));
        holder.txtAdditionalBenefits.setText("Additional Benefits : "+plan.get("Additional Benefits"));
        holder.txtTalktime.setText("Talktime : "+plan.get("Talktime"));

        // Set click listener on the itemView
        holder.itemView.setOnClickListener(v -> {
            if (onPlanClickListener != null) {
                String amount = plan.get("amountInRupees"); // Get the amount
               // onPlanClickListener.onPlanClick(amount, plan.get("desc"), plan.get("validity"));
                onPlanClickListener.onPlanClick(plan);
            }
        });
    }


    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtRs, txtDesc, txtValidity,txtAdditionalBenefits,txtTalktime;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRs = itemView.findViewById(R.id.txtRs);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtValidity = itemView.findViewById(R.id.txtValidity);
            txtAdditionalBenefits= itemView.findViewById(R.id.txtAdditionalDenefits);
            txtTalktime= itemView.findViewById(R.id.txtTalkTime);
        }
    }
}
