package com.onezed.Adapter;

import static com.onezed.GlobalVariable.GlobalVariable.selectedBillerId;
import static com.onezed.GlobalVariable.GlobalVariable.selectedBillerName;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Fragment.BBPSFragment;
import com.onezed.Fragment.SubscriptionCustomerFragment;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {
    private ArrayList<HashMap<String, String>> planList;
    private SubscriptionTabAdapter.OnPlanClickListener onPlanClickListener;
    private FragmentManager fragmentManager;

    public SubscriptionPlanAdapter(ArrayList<HashMap<String, String>> planList,FragmentManager fragmentManager, SubscriptionTabAdapter.OnPlanClickListener onPlanClickListener) {
        this.planList = planList;
        this.fragmentManager = fragmentManager;
        this.onPlanClickListener = onPlanClickListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        HashMap<String, String> plan = planList.get(position);
        Log.v("planSelected",planList.toString());
        holder.txtRs.setText("\u20B9 " + plan.get("amountInRupees"));
        holder.txtDesc.setText("Package Duration : "+plan.get("Package Duration"));
        String packageName = plan.get("Package Name");
        String description = plan.get("description");

        if (packageName != null && !packageName.equalsIgnoreCase("null")) {
            holder.txtValidity.setText("Package Name : " + packageName);
        } else {
            holder.txtValidity.setVisibility(View.GONE);
           // holder.txtValidity.setText(""); // Or
        }

        if (description != null && !description.equalsIgnoreCase("null")) {
            holder.txtDescription.setText("Description : " + description);
        } else {
            holder.txtDescription.setVisibility(View.GONE);
            // holder.txtValidity.setText(""); // Or
        }


//        if (!plan.get("description").equals("null")){
//            holder.txtDescription.setText("Description : "+ plan.get("description"));
//        }

//        holder.txtAdditionalBenefits.setText("Additional Benefits : "+plan.get("Additional Benefits"));
//        holder.txtTalktime.setText("Talktime : "+plan.get("Talktime"));

        // Set click listener on the itemView
        holder.itemView.setOnClickListener(v -> {
            if (onPlanClickListener != null) {
                GlobalVariable.isSelected=true;
                Log.v("planSelected","planSelected"+plan);
                String amount = plan.get("amountInRupees"); // Get the amount
               // onPlanClickListener.onPlanClick(amount, plan.get("desc"), plan.get("validity"));
              //  onPlanClickListener.onPlanClick(planList.get(holder.getAdapterPosition()));
                  onPlanClickListener.onPlanClick(plan);


//                BBPSFragment fm = new BBPSFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("planAmount", plan.get("amount"));  // Pass data
//                fm.setArguments(bundle);
//
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(R.id.recharge_frameLayout, fm);
//                transaction.addToBackStack(null);
//                transaction.commit();



            }
        });
    }


    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtRs, txtDesc, txtValidity,txtAdditionalBenefits,txtTalktime,txtDescription;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRs = itemView.findViewById(R.id.txtRs);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtValidity = itemView.findViewById(R.id.txtValidity);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
