
package com.example.a402313990_smartpantrymanager;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PantryPreferencesActivity extends AppCompatActivity {

    public static final String PREFS = "smart_pantry_preferences";
    public static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";
    public static final String KEY_QUANTITY_UNIT = "preferred_quantity_unit";

    private Switch switchExpiryReminders;
    private Spinner spinnerPreferredUnit;
    private SharedPreferences preferences;
    private boolean loadingSettings = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_preferences);

        // Shared navigation bar. Settings is the current tab on this screen
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setup(this, bottomNav, R.id.nav_settings);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        switchExpiryReminders = findViewById(R.id.switchExpiryReminders);
        spinnerPreferredUnit = findViewById(R.id.spinnerPreferredUnit);
        Button clearPantry = findViewById(R.id.btnClearPantry);

        TextView textAppVersion = findViewById(R.id.textAppVersion);

        // Defines the desired unit of measurement.
        String[] units = {"Grams (g)", "Kilograms (kg)"};

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPreferredUnit.setAdapter(unitAdapter);

        String savedUnit = preferences.getString(KEY_QUANTITY_UNIT, "Grams (g)");

        int selectedPosition = savedUnit.equals("Kilograms (kg)") ? 1 : 0;
        spinnerPreferredUnit.setSelection(selectedPosition);

        // Restores saved expiry reminder setting.
        switchExpiryReminders.setChecked(
                preferences.getBoolean(KEY_EXPIRY_REMINDERS, true)
        );

        loadingSettings = false;

        //Apply changes to the expiry reminder setting.
        switchExpiryReminders.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit()
                    .putBoolean(KEY_EXPIRY_REMINDERS, checked)
                    .apply();

            Toast.makeText(
                    this,
                    checked
                            ? "Expiry alerts turned on"
                            : "Expiry alerts turned off",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Saves the preferred quantity unit.
        spinnerPreferredUnit.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {
                        if (!loadingSettings) {
                            String selectedUnit = units[position];

                            preferences.edit()
                                    .putString(KEY_QUANTITY_UNIT, selectedUnit)
                                    .apply();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        // Displays the app information.
        textAppVersion.setText("Smart Pantry Manager\nVersion 1.0");

        // Confirms with the user before deleting pantry ingredients.
        clearPantry.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear pantry?")
                        .setMessage(
                                "This permanently deletes all ingredients " +
                                        "saved in your pantry. Your recipe collection " +
                                        "and saved preferences will remain unchanged."
                        )
                        .setNegativeButton("Cancel", (dialog, which) ->
                                dialog.dismiss())
                        .setPositiveButton("Clear pantry", (dialog, which) -> {
                            SmartPantryStorage storage =
                                    new SmartPantryStorage(this);

                            int deleted;
                            try {
                                deleted = storage.deleteAllIngredients();
                            } finally {
                                storage.close();
                            }

                            Toast.makeText(
                                    this,
                                    deleted + " ingredient(s) removed",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();
                        })
                        .show()
        );
    }
}
