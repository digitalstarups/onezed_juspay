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

import com.bumptech.glide.Glide;
import com.onezed.Model.FastagProviderModel;
import com.onezed.R;

import java.util.ArrayList;
import java.util.List;

public class FastagProviderAdapter extends ArrayAdapter<FastagProviderModel> implements Filterable {
    private List<FastagProviderModel> originalBillerList;
    private List<FastagProviderModel> filteredBillerList;
    private FastagProviderAdapter.BillerFilter billerFilter;

    public FastagProviderAdapter(@NonNull Context context, @NonNull List<FastagProviderModel> billers) {
        super(context, 0, billers);
        this.originalBillerList = new ArrayList<>(billers); // Store the original list
        this.filteredBillerList = billers; // Initially, filtered list is the same as original
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        FastagProviderModel biller = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.biller_child, parent, false);
        }

        // Lookup view for data population
        TextView tvServiceProvider = convertView.findViewById(R.id.name);
        ImageView image=convertView.findViewById(R.id.image_img);
        //TextView tvSpKey = convertView.findViewById(R.id.tv_sp_key);

        // Populate the data into the template view using the Biller object
        tvServiceProvider.setText(biller.getServiceProvider());
        Glide.with(getContext())
                .load(biller.getImage())  // Network image URL
                .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                .into(image);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return filteredBillerList.size();
    }

    @Nullable
    @Override
    public FastagProviderModel getItem(int position) {
        return filteredBillerList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (billerFilter == null) {
            billerFilter = new FastagProviderAdapter.BillerFilter();
        }
        return billerFilter;
    }

    private class BillerFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<FastagProviderModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalBillerList); // No filter applied, show all
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FastagProviderModel biller : originalBillerList) {
                    if (biller.getServiceProvider().toLowerCase().contains(filterPattern)) {
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
            filteredBillerList.addAll((List<FastagProviderModel>) results.values);
            notifyDataSetChanged();
        }
    }

}