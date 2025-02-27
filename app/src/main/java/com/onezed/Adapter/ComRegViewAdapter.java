package com.onezed.Adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.R;

import java.util.List;

public class ComRegViewAdapter extends RecyclerView.Adapter<ComRegViewAdapter.ItemViewHolder> {

    private final List<String> itemList;
    private final Context context;

    public ComRegViewAdapter(Context context, List<String> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.com_reg_item_list, parent, false);
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String text = itemList.get(position);

        // Create a SpannableStringBuilder to add margin at the beginning of each line
        SpannableStringBuilder spannable = new SpannableStringBuilder("\u2705  " + text);

        // Convert 10dp to pixels for the subsequent lines margin
        float density = holder.itemView.getContext().getResources().getDisplayMetrics().density;
        int marginInPx = (int) (10 * density); // 10dp to pixels

        // Add a LeadingMarginSpan to ensure the subsequent lines have 10dp margin
        spannable.setSpan(new LeadingMarginSpan.Standard(marginInPx, marginInPx), 0, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        // Set the spannable string to the TextView
        holder.itemText.setText(spannable);
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
        }
    }
}

