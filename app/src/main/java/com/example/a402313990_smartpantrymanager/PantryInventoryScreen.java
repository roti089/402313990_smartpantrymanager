package com.example.a402313990_smartpantrymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *Custom RecyclerView adapter for the pantry list items.
 *The row has an editable and deleteable item that can display an expiry warning.
 */
public class PantryInventoryScreen
        extends RecyclerView.Adapter<PantryInventoryScreen.PantryRowHolder> {

    /** Items expiring within this many days will be highlighted when expiry warnings are enabled.*/
    private static final int EXPIRING_SOON_DAYS = 3;

    private final SmartPantryStorage storage;
    private final Runnable onChanged;
    private final List<PantryIngredient> ingredients = new ArrayList<>();

    // Values from the settings screen
    private boolean expiryAlertsEnabled = true;
    private boolean preferKilograms = false;

    public PantryInventoryScreen(SmartPantryStorage storage, Runnable onChanged) {
        this.storage = storage;
        this.onChanged = onChanged;
    }

    /** Replaces the displayed ingredients and refreshes the list. */
    public void setIngredients(List<PantryIngredient> items) {
        ingredients.clear();
        ingredients.addAll(items);
        notifyDataSetChanged();
    }

    /** Sets up the user’s settings, including the list displaying expiry warnings and weight
     * unit preference. */
    public void setPreferences(boolean expiryAlertsEnabled, boolean preferKilograms) {
        this.expiryAlertsEnabled = expiryAlertsEnabled;
        this.preferKilograms = preferKilograms;
    }

    @NonNull
    @Override
    public PantryRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry_ingredient, parent, false);
        return new PantryRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryRowHolder holder, int position) {
        PantryIngredient item = ingredients.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText(item.getDisplayAmount(preferKilograms));
        holder.details.setText(buildDetailsText(item));
        bindExpiryAlert(holder, item);

        holder.buttonEdit.setOnClickListener(v -> openEditor(v.getContext(), item));
        holder.buttonDelete.setOnClickListener(v -> confirmDelete(v.getContext(), item));
    }

    /** Category and expiry date */
    private String buildDetailsText(PantryIngredient item) {
        String expiry = item.getExpiryDate().trim();
        if (expiry.isEmpty()) {
            return item.getCategory() + "  \u2022  No expiry date";
        }
        return item.getCategory() + "  \u2022  Expiry: " + toDisplayDate(expiry);
    }

    /** Shows or hides the orange/red expiry warning under the row details. */
    private void bindExpiryAlert(PantryRowHolder holder, PantryIngredient item) {
        holder.alert.setVisibility(View.GONE);

        if (!expiryAlertsEnabled) {
            return;
        }

        Integer days = item.getDaysUntilExpiry();
        if (days == null || days > EXPIRING_SOON_DAYS) {
            return;
        }

        if (days < 0) {
            holder.alert.setText("\u26A0 Expired " + (-days) + (days == -1 ? " day ago" : " days ago"));
            holder.alert.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.pantry_danger));
        } else {
            String when;
            if (days == 0) {
                when = "today";
            } else if (days == 1) {
                when = "tomorrow";
            } else {
                when = "in " + days + " days";
            }
            holder.alert.setText("\u26A0 Expires " + when);
            holder.alert.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.pantry_orange));
        }
        holder.alert.setVisibility(View.VISIBLE);
    }

    /** UPDATE, opens the editor and passes the ingredient id with an Intent extra. */
    private void openEditor(Context context, PantryIngredient item) {
        Intent intent = new Intent(context, IngredientEditorActivity.class);
        intent.putExtra(IngredientEditorActivity.EXTRA_INGREDIENT_ID, item.getId());
        context.startActivity(intent);
    }

    /** DELETE, asks for confirmation first, then removes the ingredient and refreshes the list. */
    private void confirmDelete(Context context, PantryIngredient item) {
        new AlertDialog.Builder(context)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    storage.deleteIngredient(item.getId());
                    Toast.makeText(context, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    onChanged.run();
                })
                .show();
    }

    /** This converts the stored yyyy-MM-dd date into dd/MM/yyyy for display. */
    private String toDisplayDate(String isoDate) {
        try {
            SimpleDateFormat stored = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = stored.parse(isoDate);
            if (date != null) {
                return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
            }
        } catch (ParseException ignored) {
            // fall through and show the stored text
        }
        return isoDate;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    /** Holds the views of one pantry row. */
    static class PantryRowHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView details;
        final TextView alert;
        final ImageButton buttonEdit;
        final ImageButton buttonDelete;

        PantryRowHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textIngredientName);
            quantity = itemView.findViewById(R.id.textIngredientQuantity);
            details = itemView.findViewById(R.id.textIngredientDetails);
            alert = itemView.findViewById(R.id.textIngredientAlert);
            buttonEdit = itemView.findViewById(R.id.btnEditIngredient);
            buttonDelete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
