package com.onezed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.onezed.R;

import java.util.List;

public class WhatWeProvideAdapter extends RecyclerView.Adapter<WhatWeProvideAdapter.ViewHolder> {

    private List<String> whatWeProvideList;
    private Context context;

    public WhatWeProvideAdapter(List<String> whatWeProvideList, Context context) {
        this.whatWeProvideList = whatWeProvideList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_what_we_provide, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String whatWeProvide = whatWeProvideList.get(position);
        holder.tvWhatWeProvide.setText(whatWeProvide);
    }

    @Override
    public int getItemCount() {
        return whatWeProvideList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWhatWeProvide;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWhatWeProvide = itemView.findViewById(R.id.tvWhatWeProvide);
        }
    }
}

