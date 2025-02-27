package com.onezed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onezed.Model.CustomerParameterBiller;
import com.onezed.R;

import java.util.ArrayList;
import java.util.List;

public class FaderalBillerRechargeAdapter extends ArrayAdapter<CustomerParameterBiller> implements Filterable {
    private List<CustomerParameterBiller> originalBillerList;
    private List<CustomerParameterBiller> filteredBillerList;
    private BillerFilter billerFilter;
    private OnBillerClickListener billerClickListener;  // Listener

    public FaderalBillerRechargeAdapter(@NonNull Context context, int resorce, @NonNull List<CustomerParameterBiller> billers, OnBillerClickListener listener) {
        super(context, resorce, billers);
        this.originalBillerList = new ArrayList<>(billers); // Store the original list
        this.filteredBillerList = billers; // Initially, filtered list is the same as original
        this.billerClickListener = listener; // Set the listener
    }

//    public FaderalBillerAdapter(Context context, int simpleListItem1, List<CustomerParameterBiller> billerList) {
//        super();
//    }
// Define the interface for the listener
    public interface OnBillerClickListener {
        void onBillerClick(CustomerParameterBiller biller);  // Define what happens on item click
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        CustomerParameterBiller biller = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recharge_biller_child, parent, false);
        }

        // Lookup view for data population
        TextView tvServiceProvider = convertView.findViewById(R.id.name);
        ImageView image=convertView.findViewById(R.id.image_img);
        //TextView tvSpKey = convertView.findViewById(R.id.tv_sp_key);

        // Populate the data into the template view using the Biller object
        tvServiceProvider.setText(biller.getBillerName());
//        Glide.with(getContext())
//                .load(biller.getImage())  // Network image URL
//                .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
//                .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
//                .into(image);

        // Return the completed view to render on screen
        convertView.setOnClickListener(v -> {
            if (billerClickListener != null) {
                billerClickListener.onBillerClick(biller);  // Trigger listener
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return filteredBillerList.size();
    }

    @Nullable
    @Override
    public CustomerParameterBiller getItem(int position) {
        return filteredBillerList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (billerFilter != null) {
            billerFilter = new BillerFilter();
        }
        return billerFilter;
    }

    private class BillerFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CustomerParameterBiller> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalBillerList); // No filter applied, show all
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CustomerParameterBiller biller : originalBillerList) {
                    if (biller.getBillerName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(biller);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredBillerList.clear();
            filteredBillerList.addAll((List<CustomerParameterBiller>) results.values);
            notifyDataSetChanged();
        }
    }


    public void filter(String query) {
        query = query.toLowerCase().trim();
       // filteredBillerList.clear();

        if (query.isEmpty()) {
            filteredBillerList.addAll(originalBillerList);
        } else {
            for (CustomerParameterBiller biller : originalBillerList) {
                if (biller.getBillerName().toLowerCase().contains(query)) {
                    filteredBillerList.add(biller);
                    filteredBillerList.addAll((List<CustomerParameterBiller>) biller);
                }
            }
        }

        // Notify the adapter that the data has changed
        notifyDataSetChanged();
    }


}
