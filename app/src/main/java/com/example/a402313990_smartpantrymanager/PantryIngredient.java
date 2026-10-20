package com.example.a402313990_smartpantrymanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** One item from the user’s pantry which is a row from the pantry_ingredients table. */
public class PantryIngredient {
    private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

    private final int id;
    private final String name;

    private final double quantity;
    private final String unit;
    private final String category;
    private final String expiryDate; // This uses yyyy-MM-dd format or "" when there is no expiry entered

    public PantryIngredient(int id, String name, double quantity, String unit,
                            String category, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.expiryDate = expiryDate == null ? "" : expiryDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getCategory() { return category; }
    public String getExpiryDate() { return expiryDate; }

    /**
     *Formats the quantity without unnecessary decimals; that is, 4.0 is formatted to "4", while 0.25 is still "0.25"
     *Uses only dot as a decimal separator to enable re-parsing.
     */
    public static String formatQuantity(double value) {
        if (value == Math.rint(value)) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.format(Locale.US, "%.3f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    /**
     * The number and the unit as provided in the pantry list. The weights will be converted into the
     * unit selected in Settings (grams or kilograms); all other units will be displayed as stored.
     */
    public String getDisplayAmount(boolean preferKilograms) {
        String lowerUnit = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        boolean isGrams = lowerUnit.equals("g") || lowerUnit.startsWith("gram");
        boolean isKilograms = lowerUnit.equals("kg") || lowerUnit.startsWith("kilogram");

        if (preferKilograms && isGrams) {
            return formatQuantity(quantity / 1000) + " kg";
        }
        if (!preferKilograms && isKilograms) {
            return formatQuantity(quantity * 1000) + " g";
        }
        return formatQuantity(quantity) + " " + unit;
    }

    /**
     * Days from today to expiry date, negative if already expired, zero if it expires today.
     * Null if no expiry date for the ingredient.
     */
    public Integer getDaysUntilExpiry() {
        if (expiryDate.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date expiry = format.parse(expiryDate.trim());
            if (expiry == null) {
                return null;
            }

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long difference = expiry.getTime() - today.getTimeInMillis();
            return (int) Math.round(difference / (double) MILLIS_PER_DAY);
        } catch (ParseException e) {
            return null;
        }
    }
}
