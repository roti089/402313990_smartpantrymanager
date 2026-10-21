package com.example.a402313990_smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/** The Recipe Detail screen displays the entire ingredient list and method of a single recipe. */
public class RecipeDetailActivity extends AppCompatActivity {

    /** Extra intent key that has the id of the recipe to be shown. */
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private SmartPantryStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView textName = findViewById(R.id.textRecipeDetailName);
        TextView textDescription = findViewById(R.id.textRecipeDetailDescription);
        TextView textMeta = findViewById(R.id.textRecipeDetailMeta);
        TextView textIngredients = findViewById(R.id.textRecipeIngredients);
        TextView textInstructions = findViewById(R.id.textRecipeInstructions);

        findViewById(R.id.btnRecipeDetailBack).setOnClickListener(v -> finish());

        //Identify the recipe id that has been passed to the intent and show it.
        storage = new SmartPantryStorage(this);
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = storage.getRecipeById(recipeId);

        if (recipe == null) {
            textName.setText("Recipe not found");
            textDescription.setText("Return to suggested recipes and choose a recipe again.");
            textMeta.setText("");
            textIngredients.setText("");
            textInstructions.setText("");
            return;
        }

        textName.setText(recipe.getName());
        textDescription.setText(recipe.getDescription());
        textMeta.setText(recipe.getCategory() + "  \u2022  "
                + recipe.getPreparationMinutes() + " minutes  \u2022  " + recipe.getDifficulty());

        // One bullet line per ingredient, e.g. "\u2022 Eggs \u2014 2 pieces"
        StringBuilder list = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            list.append("\u2022 ")
                    .append(ingredient.getDisplayName())
                    .append(" \u2014 ")
                    .append(ingredient.getDisplayAmount())
                    .append("\n");
        }
        textIngredients.setText(list.toString().trim());
        textInstructions.setText(recipe.getInstructions());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (storage != null) {
            storage.close();
        }
    }
}
