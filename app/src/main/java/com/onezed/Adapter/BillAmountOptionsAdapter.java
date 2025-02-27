package com.onezed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.onezed.Model.BillAmountOptionsModel;
import com.onezed.R;

import java.util.List;


public class BillAmountOptionsAdapter extends BaseAdapter {

    private Context context;
    private List<BillAmountOptionsModel> billAmountOptionsList;
    private FragmentCommunication fragmentCommunication;
    private int selectedPosition = -1;  // Keep track of selected position

    public BillAmountOptionsAdapter(Context context, List<BillAmountOptionsModel> billAmountOptionsList, FragmentCommunication fragmentCommunication) {
        this.context = context;
        this.billAmountOptionsList = billAmountOptionsList;
        this.fragmentCommunication = fragmentCommunication;

        // Set the default selection to the last item
        this.selectedPosition = billAmountOptionsList.size() - 1;

        // Automatically trigger the selection for the last item
        if (!billAmountOptionsList.isEmpty()) {
            fragmentCommunication.onAmountSelected(billAmountOptionsList.get(selectedPosition).getAmountValue());
        }
    }


    @Override
    public int getCount() {
        return billAmountOptionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return billAmountOptionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bill_amount_option_item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.tvAmountName);
        TextView amount = convertView.findViewById(R.id.tvAmountValue);
        RadioButton radioButton = convertView.findViewById(R.id.radioButton);

        // Bind data to views
        BillAmountOptionsModel model = billAmountOptionsList.get(position);
        name.setText(model.getAmountName());
        amount.setText("\u20B9 " + model.getAmountValue());

        // Ensure only the selected radio button is checked
        radioButton.setChecked(position == selectedPosition);

        // RadioButton click listener
        radioButton.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();  // Refresh the list to show the updated selection

            // Invoke the callback method
            fragmentCommunication.onAmountSelected(model.getAmountValue());
        });

        return convertView;
    }



    public interface FragmentCommunication {

        void onAmountSelected(String selectedAmount);
    }
}

