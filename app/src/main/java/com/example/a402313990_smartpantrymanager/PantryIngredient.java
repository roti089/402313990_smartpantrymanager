package com.example.a402313990_smartpantrymanager;

public class PantryIngredient {

    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String category;
    private String expiryDate;

    public PantryIngredient(
            int id,
            String name,
            double quantity,
            String unit,
            String category,
            String expiryDate) {

        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getCategory() {
        return category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}