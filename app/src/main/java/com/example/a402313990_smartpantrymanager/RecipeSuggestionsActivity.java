package com.example.a402313990_smartpantrymanager;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Suggested Recipes screen with two clearly separated views:
 *  - "Ready now": the STRICT matches (every ingredient available). This is the main list.
 *  - "Almost there": optional bonus list of recipes missing exactly one ingredient.
 * The search box and category filter only narrow down the list being shown.
 */
public class RecipeSuggestionsActivity extends AppCompatActivity {

    private static final String all_categories = "All categories";

    private SmartPantryStorage storage;
    private RecyclerView recyclerRecipes;
    private TextView textEmpty;
    private TextView subtitle;
    private EditText editSearch;
    private Spinner spinnerCategory;
    private Button readyBtn;
    private Button almostBtn;
    private RecipeSuggestionAdapter adapter;

    /** Recipes that passed the strict-matching rule. */
    private List<Recipe> strictMatches = new ArrayList<>();

    /** Recipes missing exactly one ingredient (bonus list). */
    private List<RecipeMatch.AlmostThereMatch> almostThere = new ArrayList<>();

    /** Which of the two lists is currently displayed. */
    private boolean showingAlmostThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_suggestions);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        textEmpty = findViewById(R.id.textRecipesEmpty);
        subtitle = findViewById(R.id.textRecipesSubtitle);
        editSearch = findViewById(R.id.editRecipeSearch);
        spinnerCategory = findViewById(R.id.spinnerRecipeCategory);
        readyBtn = findViewById(R.id.btnReadyNow);
        almostBtn = findViewById(R.id.btnAlmostThere);

        storage = new SmartPantryStorage(this);

        adapter = new RecipeSuggestionAdapter();
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecipes.setAdapter(adapter);

        setupCategorySpinner();
        setupSearchBox();

        readyBtn.setOnClickListener(v -> switchList(false));
        almostBtn.setOnClickListener(v -> switchList(true));

        // Shared navigation bar,recipes is the current tab on this screen
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setup(this, bottomNav, R.id.nav_recipes);
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                all_categories, "Breakfast", "Lunch", "Dinner", "Snack");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSearchBox() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * The matching is re-run each time the screen comes into view, ensuring that the lists are
     * always up-to-date after the pantry has been modified on some other screen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshMatches();
    }

    /** Reads the pantry and recipes from the database and runs both matching rules. */
    private void refreshMatches() {
        List<PantryIngredient> pantry = storage.getAllPantryIngredients();
        List<Recipe> allRecipes = storage.getAllRecipes();

        strictMatches = RecipeMatch.findMatches(allRecipes, pantry);
        almostThere = RecipeMatch.findAlmostThere(allRecipes, pantry);

        readyBtn.setText("Ready now (" + strictMatches.size() + ")");
        almostBtn.setText("Almost there (" + almostThere.size() + ")");

        applyFilters();
    }

    /** Switches between the strict list and the "Almost there" list. */
    private void switchList(boolean almost) {
        showingAlmostThere = almost;
        applyFilters();
    }

    /** Applies the search text and category to the chosen list and updates the screen. */
    private void applyFilters() {
        updateToggleStyle();

        String query = editSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        Object selected = spinnerCategory.getSelectedItem();
        String category = (selected == null) ? all_categories : selected.toString();

        // Build the source list for the view that is currently selected
        List<Recipe> source = new ArrayList<>();
        Map<Integer, String> notes = new HashMap<>();
        if (showingAlmostThere) {
            for (RecipeMatch.AlmostThereMatch match : almostThere) {
                source.add(match.recipe);
                notes.put(match.recipe.getId(), "Missing: " + match.missing.getDisplayName()
                        + " (need " + match.missing.getDisplayAmount() + ")");
            }
        } else {
            source.addAll(strictMatches);
        }

        List<Recipe> shown = new ArrayList<>();
        for (Recipe recipe : source) {
            boolean matchesText = recipe.getName().toLowerCase(Locale.ROOT).contains(query)
                    || recipe.getDescription().toLowerCase(Locale.ROOT).contains(query);
            boolean matchesCategory = category.equals(all_categories)
                    || recipe.getCategory().equalsIgnoreCase(category);

            if (matchesText && matchesCategory) {
                shown.add(recipe);
            }
        }

        adapter.setRecipes(shown, showingAlmostThere ? notes : null);

        if (shown.isEmpty()) {
            textEmpty.setText(emptyMessage(source.isEmpty()));
            textEmpty.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
        }
    }

    /** Friendly message instead of a blank screen when there are no recipes.  */
    private String emptyMessage(boolean listItselfIsEmpty) {
        if (!listItselfIsEmpty) {
            return "No recipes match this search or category.";
        }
        return showingAlmostThere
                ? "No recipes are missing just one ingredient right now."
                : "No recipes match your pantry yet - add more ingredients.";
    }

    /** Highlights the selected button and updates the subtitle under the title. */
    private void updateToggleStyle() {
        styleToggleButton(readyBtn, !showingAlmostThere);
        styleToggleButton(almostBtn, showingAlmostThere);

        subtitle.setText(showingAlmostThere
                ? "Recipes missing just one ingredient. Not ready to cook yet."
                : "Only meals you can make with your current pantry.");
    }

    private void styleToggleButton(Button button, boolean selected) {
        int background = ContextCompat.getColor(this,
                selected ? R.color.pantry_teal : R.color.pantry_white);
        int text = ContextCompat.getColor(this,
                selected ? R.color.pantry_white : R.color.pantry_navy);
        button.setBackgroundTintList(ColorStateList.valueOf(background));
        button.setTextColor(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (storage != null) {
            storage.close();
        }
    }
}
