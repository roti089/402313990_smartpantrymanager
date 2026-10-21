package com.example.a402313990_smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

/** This is the home screen which lists the pantry ingredients and links to the other screens. */
public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fabAdd;
    private BottomNavigationView bottomNav;
    private TextView textEmpty;
    private RecyclerView recyclerPantry;
    private SmartPantryStorage pantryStorage;
    private PantryInventoryScreen pantryDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);
        bottomNav = findViewById(R.id.bottomNav);
        textEmpty = findViewById(R.id.textEmpty);
        recyclerPantry = findViewById(R.id.recyclerPantry);

        pantryStorage = new SmartPantryStorage(this);
        pantryDisplay = new PantryInventoryScreen(pantryStorage, this::loadPantryIngredients);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(pantryDisplay);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, IngredientEditorActivity.class)));

        // Shared navigation bar: Pantry is the current tab on this screen
        BottomNavHelper.setup(this, bottomNav, R.id.nav_pantry);
    }

    /**
     * Occurs each time the screen appears even on returning from other screens,
     * therefore the list and Settings screen are always current.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryIngredients();
    }

    private void loadPantryIngredients() {
        List<PantryIngredient> list = pantryStorage.getAllPantryIngredients();

        // Checks the Settings screen options so that the list is based on them
        SharedPreferences prefs = getSharedPreferences(
                PantryPreferencesActivity.PREFS, MODE_PRIVATE);
        boolean expiryAlerts = prefs.getBoolean(
                PantryPreferencesActivity.KEY_EXPIRY_REMINDERS, true);
        boolean preferKilograms = "Kilograms (kg)".equals(prefs.getString(
                PantryPreferencesActivity.KEY_QUANTITY_UNIT, "Grams (g)"));
        pantryDisplay.setPreferences(expiryAlerts, preferKilograms);

        pantryDisplay.setIngredients(list);
        textEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pantryStorage != null) {
            pantryStorage.close();
        }
    }
}
