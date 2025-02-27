package com.onezed.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onezed.R;

import java.util.ArrayList;
import java.util.List;

public class contact_adapter extends ArrayAdapter<String> implements Filterable {
    private Activity context;
    private List<String> name;
    private List<String> ph;
    private List<String> filteredName;
    private List<String> filteredPh;
    private ContactFilter filter;

    public contact_adapter(Activity context, List<String> names, List<String> ph) {
        super(context, R.layout.contact_ui, ph);
        this.context = context;

        // Remove duplicates based on phone numbers
        List<String> uniquePh = new ArrayList<>();
        List<String> uniqueNames = new ArrayList<>();
        for (int i = 0; i < ph.size(); i++) {
            if (!uniquePh.contains(ph.get(i))) {
                uniquePh.add(ph.get(i));
                uniqueNames.add(names.get(i));
            }
        }

        this.name = uniqueNames;
        this.ph = uniquePh;
        this.filteredName = new ArrayList<>(uniqueNames);
        this.filteredPh = new ArrayList<>(uniquePh);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.contact_ui, null, true);
        TextView Textname = rowView.findViewById(R.id.name);
        TextView Textph = rowView.findViewById(R.id.ph);

        // Check if the position is valid
        if (position >= 0 && position < filteredName.size()) {
            Textname.setText(filteredName.get(position));
            Textph.setText(filteredPh.get(position));
        } else {
            Textname.setText("");
            Textph.setText("");
        }
        return rowView;
    }

    @Override
    public int getCount() {
        return filteredName.size(); // Return the size of the filtered list
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ContactFilter();
        }
        return filter;
    }

    // Method to get name for a given position
    public String getName(int position) {
        if (position >= 0 && position < filteredName.size()) {
            return filteredName.get(position);
        }
        return null;
    }

    // Method to get phone number for a given position
    public String getPhoneNumber(int position) {
        if (position >= 0 && position < filteredPh.size()) {
            return filteredPh.get(position);
        }
        return null;
    }
    public boolean containsNumber(String number) {
        for (int i = 0; i < getCount(); i++) {
            if (getPhoneNumber(i).equals(number)) {
                return true;
            }
        }
        return false;
    }
    public boolean isEmpty() {
        return getCount() == 0;
    }

    private OnFilterCompleteListener filterCompleteListener;

    public interface OnFilterCompleteListener {
        void onFilterComplete(boolean isEmpty);
    }

    public void setOnFilterCompleteListener(OnFilterCompleteListener listener) {
        this.filterCompleteListener = listener;
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> filteredNames = new ArrayList<>();
            List<String> filteredPhones = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                for (int i = 0; i < name.size(); i++) {
                    if (!filteredPhones.contains(ph.get(i))) {
                        filteredNames.add(name.get(i));
                        filteredPhones.add(ph.get(i));
                    }
                }
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (int i = 0; i < name.size(); i++) {
                    if ((name.get(i).toLowerCase().contains(filterPattern) ||
                            ph.get(i).toLowerCase().contains(filterPattern)) &&
                            !filteredPhones.contains(ph.get(i))) {
                        filteredNames.add(name.get(i));
                        filteredPhones.add(ph.get(i));
                    }
                }
            }

            results.values = new Object[]{filteredNames, filteredPhones};
            results.count = filteredNames.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredName.clear();
            filteredPh.clear();

            if (results.values != null) {
                Object[] filteredData = (Object[]) results.values;
                filteredName.addAll((List<String>) filteredData[0]);
                filteredPh.addAll((List<String>) filteredData[1]);
            }

            // Notify adapter of data changes
            notifyDataSetChanged();

            // Notify the listener whether the filtered list is empty
            if (filterCompleteListener != null) {
                filterCompleteListener.onFilterComplete(filteredName.isEmpty());
            }
        }
    }

}
