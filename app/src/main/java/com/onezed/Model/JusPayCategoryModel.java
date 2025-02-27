package com.onezed.Model;

public class JusPayCategoryModel {
    private String categoryName;
    private String categoryId;
    private String categoryIcon; // Base64 encoded icon

    // Constructor
    public JusPayCategoryModel(String categoryId , String categoryName, String categoryIcon) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.categoryIcon = categoryIcon;
    }

    // Getters
    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }
}

