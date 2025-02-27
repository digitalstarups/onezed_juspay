package com.onezed.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.onezed.Model.CustomerParameterBiller;

import java.util.ArrayList;
import java.util.List;

public class BillerAdapterFilter extends ArrayAdapter<CustomerParameterBiller> {
    private List<CustomerParameterBiller> originalList;
    private List<CustomerParameterBiller> filteredList;

    public BillerAdapterFilter(Context context, int resource, List<CustomerParameterBiller> objects) {
        super(context, resource, objects);
        this.originalList = new ArrayList<>(objects); // Full list
        this.filteredList = objects; // Displayed list
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public CustomerParameterBiller getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    // No filter applied
                    results.values = originalList;
                    results.count = originalList.size();
                } else {
                    String filterString = constraint.toString().toLowerCase().trim();
                    List<CustomerParameterBiller> filteredResults = new ArrayList<>();

                    for (CustomerParameterBiller biller : originalList) {
                        if (biller.getBillerName().toLowerCase().contains(filterString)) {
                            filteredResults.add(biller);
                        }
                    }

                    results.values = filteredResults;
                    results.count = filteredResults.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<CustomerParameterBiller>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
