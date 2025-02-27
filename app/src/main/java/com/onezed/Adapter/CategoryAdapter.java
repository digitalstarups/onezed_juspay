package com.onezed.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Model.JusPayCategoryModel;
import com.onezed.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<JusPayCategoryModel> categoryList;

    // Constructor
    public CategoryAdapter(List<JusPayCategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        JusPayCategoryModel category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryNameTextView;
        private ImageView categoryIconImageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            categoryIconImageView = itemView.findViewById(R.id.categoryIconImageView);
        }

        public void bind(JusPayCategoryModel category) {
            categoryNameTextView.setText(category.getCategoryName());
            // Decode Base64 icon and set it to ImageView
            byte[] decodedString = Base64.decode(category.getCategoryIcon(), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            categoryIconImageView.setImageBitmap(decodedBitmap);
        }
    }
}
