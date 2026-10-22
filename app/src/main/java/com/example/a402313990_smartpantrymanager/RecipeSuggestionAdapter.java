package com.example.a402313990_smartpantrymanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Custom adapter that displays each recipe suggestion in card format item_recipe.xml. */
public class RecipeSuggestionAdapter
        extends RecyclerView.Adapter<RecipeSuggestionAdapter.RecipeHolder> {

    private final List<Recipe> recipes = new ArrayList<>();

    /** Recipe id -> "Missing" text. Only filled for the "Almost there" list. */
    private final Map<Integer, String> missingNotes = new HashMap<>();

    /** Replaces the displayed recipes with a strict "Ready now" list and refreshes the list. */
    public void setRecipes(List<Recipe> values) {
        setRecipes(values, null);
    }

    /**
     * Replaces the displayed recipes and refreshes the list.
     * These replace the recipes shown on them. Where there are notes,
     * each one indicates what is missing rather than “all ingredients available.”
     */
    public void setRecipes(List<Recipe> values, Map<Integer, String> notes) {
        recipes.clear();
        recipes.addAll(values);
        missingNotes.clear();
        if (notes != null) {
            missingNotes.putAll(notes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.description.setText(recipe.getDescription());
        holder.meta.setText(recipe.getCategory() + "  \u2022  "
                + recipe.getPreparationMinutes() + " min  \u2022  " + recipe.getDifficulty());

        // Short preview such as Ingredients
        StringBuilder preview = new StringBuilder("Ingredients: ");
        List<RecipeIngredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) {
                preview.append(" \u00B7 ");
            }
            RecipeIngredient item = ingredients.get(i);
            preview.append(item.getDisplayName()).append(" ").append(item.getDisplayAmount());
        }
        holder.ingredients.setText(preview.toString());

        String missingNote = missingNotes.get(recipe.getId());
        if (missingNote != null) {
            // Almost there card with orange warning showing the one missing ingredient
            holder.match.setText("\u26A0 " + missingNote);
            holder.match.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.pantry_orange));
        } else {
            // Ready now card with green-teal tick
            holder.match.setText("\u2713 All required ingredients and quantities available");
            holder.match.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.pantry_teal));
        }

        // Open the detail screen and pass the recipe id with an Intent extra
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /** Consists of the views of one recipe card.*/
    static class RecipeHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;
        final TextView meta;
        final TextView ingredients;
        final TextView match;

        RecipeHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.textRecipeName);
            description = view.findViewById(R.id.textRecipeDescription);
            meta = view.findViewById(R.id.textRecipeMeta);
            ingredients = view.findViewById(R.id.textRecipeIngredientsPreview);
            match = view.findViewById(R.id.textRecipeMatch);
        }
    }
}
