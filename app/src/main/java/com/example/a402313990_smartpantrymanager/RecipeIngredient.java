package com.example.a402313990_smartpantrymanager;

import java.util.Locale;

/** An ingredient row in a recipe such as "eggs, 2, pieces."*/
public class RecipeIngredient {
    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    /** Conversion of the name with a capital first letter, e.g. "Peanut butter". */
    public String getDisplayName() {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    /** Amount and unit of measurement for display, e.g., "150 g" or "1.5 pieces". */
    public String getDisplayAmount() {
        String number = (quantity == Math.rint(quantity))
                ? String.valueOf((long) quantity)
                : String.format(Locale.getDefault(), "%.1f", quantity);
        return number + " " + unit;
    }
}
