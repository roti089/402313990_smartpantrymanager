package com.example.a402313990_smartpantrymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 - Add/Edit Ingredient activity.
 - If the intent includes an ingredient id, the activity edits the ingredient,
 - Otherwise, it adds a new ingredient. All inputs are validated before saving.
 */
public class IngredientEditorActivity extends AppCompatActivity {

    /** Intent extra key that carries the id of the ingredient to edit. */
    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    private static final int NO_ID = -1;

    private static final int MAX_NAME_LENGTH = 40;
    private static final double MAX_QUANTITY = 100000;

    private EditText editName;
    private EditText editQuantity;
    private Spinner spinnerUnit;
    private Spinner spinnerCategory;
    private Button btnExpiryDate;
    private Button saveBtn;
    private Button cancelBtn;
    private SmartPantryStorage storage;

    private int ingredientId = NO_ID;
    private String selectedExpiryDate = "";   // yyyy-MM-dd, or "" for no expiry
    private String originalExpiryDate = "";   // the date the ingredient had when it was opened

    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_editor);

        editName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnExpiryDate = findViewById(R.id.btnExpiryDate);
        saveBtn = findViewById(R.id.btnSaveIngredient);
        cancelBtn = findViewById(R.id.btnCancel);

        storage = new SmartPantryStorage(this);

        setupSpinner(spinnerUnit, R.array.ingredient_units);
        setupSpinner(spinnerCategory, R.array.ingredient_categories);

        btnExpiryDate.setOnClickListener(v -> showDatePicker());
        cancelBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(v -> onSaveClicked());

        // In the Intent, an id is "edit mode"; put that ingredient in the form
        ingredientId = getIntent().getIntExtra(EXTRA_INGREDIENT_ID, NO_ID);
        if (ingredientId != NO_ID) {
            loadIngredient();
        }
    }

    private void setupSpinner(Spinner spinner, int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /** Populates the form with the previously stored values for the current ingredient. */
    private void loadIngredient() {
        try (Cursor cursor = storage.getIngredient(ingredientId)) {
            if (!cursor.moveToFirst()) {
                return;
            }

            editName.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(SmartPantryStorage.COLUMN_NAME)));

            // formatQuantity shows "4" instead of "4.0"
            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(SmartPantryStorage.COLUMN_QUANTITY));
            editQuantity.setText(PantryIngredient.formatQuantity(quantity));

            setSpinnerValue(spinnerUnit, cursor.getString(
                    cursor.getColumnIndexOrThrow(SmartPantryStorage.COLUMN_UNIT)));
            setSpinnerValue(spinnerCategory, cursor.getString(
                    cursor.getColumnIndexOrThrow(SmartPantryStorage.COLUMN_CATEGORY)));

            selectedExpiryDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(SmartPantryStorage.COLUMN_EXPIRY_DATE));
            if (selectedExpiryDate == null) {
                selectedExpiryDate = "";
            }
            originalExpiryDate = selectedExpiryDate;

            updateDateButton();
            saveBtn.setText("Update ingredient");
        }
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    // Allows user to enter a date in the date picker

    private void showDatePicker() {
        Calendar start = Calendar.getInstance();
        if (!selectedExpiryDate.isEmpty()) {
            try {
                Date saved = dbFormat.parse(selectedExpiryDate);
                if (saved != null) {
                    start.setTime(saved);
                }
            } catch (ParseException ignored) {
                // Make today's date the point of departure
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, day);
            selectedExpiryDate = dbFormat.format(picked.getTime());
            updateDateButton();
        }, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));

        // Validation, an expiry date in the past cannot be chosen
        dialog.getDatePicker().setMinDate(startOfTodayMillis());
        dialog.show();
    }

    private void updateDateButton() {
        if (selectedExpiryDate.isEmpty()) {
            btnExpiryDate.setText("Choose expiry date (optional)");
            return;
        }
        try {
            Date date = dbFormat.parse(selectedExpiryDate);
            btnExpiryDate.setText(date == null ? selectedExpiryDate : displayFormat.format(date));
        } catch (ParseException e) {
            btnExpiryDate.setText(selectedExpiryDate);
        }
    }

    private long startOfTodayMillis() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTimeInMillis();
    }

    // Allows saving

    /** Checks form, alerts on duplicate name, and finally saves.*/
    private void onSaveClicked() {
        if (!validateForm()) {
            return;
        }

        String name = editName.getText().toString().trim();
        if (nameAlreadyInPantry(name)) {
            new AlertDialog.Builder(this)
                    .setTitle("Already in your pantry")
                    .setMessage(name + " is already in your pantry. Save it as a separate entry "
                            + "anyway? Recipes add the quantities of matching entries together.")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Save anyway", (dialog, which) -> persistIngredient())
                    .show();
        } else {
            persistIngredient();
        }
    }

    /** Validates all fields. Displays an error on the first fault and returns false. */
    private boolean validateForm() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();

        if (name.isEmpty()) {
            return showFieldError(editName, "Enter an ingredient name");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return showFieldError(editName, "Name must be " + MAX_NAME_LENGTH + " characters or fewer");
        }
        if (!name.matches(".*\\p{L}.*")) {
            return showFieldError(editName, "Name must contain letters");
        }

        if (quantityText.isEmpty()) {
            return showFieldError(editQuantity, "Enter a quantity");
        }
        double quantity;
        try {
            quantity = readQuantity();
        } catch (NumberFormatException e) {
            return showFieldError(editQuantity, "Enter a valid number");
        }
        if (!Double.isFinite(quantity) || quantity <= 0) {
            return showFieldError(editQuantity, "Quantity must be greater than zero");
        }
        if (quantity > MAX_QUANTITY) {
            return showFieldError(editQuantity, "Quantity is too large");
        }

        if (spinnerUnit.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Choose a unit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Choose a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        /* A new expiration date should never occur in the past. A previously stored date is acceptable,
         hence the quantity of an expired item can be edited.*/
        if (!selectedExpiryDate.isEmpty()
                && !selectedExpiryDate.equals(originalExpiryDate)
                && selectedExpiryDate.compareTo(dbFormat.format(new Date())) < 0) {
            Toast.makeText(this, "Expiry date cannot be in the past", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean showFieldError(EditText field, String message) {
        field.setError(message);
        field.requestFocus();
        return false;
    }

    /** Quantity field is read; comma used as the decimal separator. */
    private double readQuantity() {
        String text = editQuantity.getText().toString().trim().replace(',', '.');
        return Double.parseDouble(text);
    }

    /** True if ANOTHER pantry entry already has this ingredient ignores case and plurals. */
    private boolean nameAlreadyInPantry(String name) {
        String wanted = RecipeMatch.normalize(name);
        for (PantryIngredient item : storage.getAllPantryIngredients()) {
            boolean isDifferentEntry = item.getId() != ingredientId;
            if (isDifferentEntry && RecipeMatch.normalize(item.getName()).equals(wanted)) {
                return true;
            }
        }
        return false;
    }

    /** CREATE a new ingredient or UPDATE the one being edited.  */
    private void persistIngredient() {
        String name = editName.getText().toString().trim();
        double quantity = readQuantity();
        String unit = spinnerUnit.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        boolean isEditing = ingredientId != NO_ID;
        boolean success;
        if (isEditing) {
            success = storage.updateIngredient(
                    ingredientId, name, quantity, unit, category, selectedExpiryDate) > 0;
        } else {
            success = storage.addIngredient(
                    name, quantity, unit, category, selectedExpiryDate) != -1;
        }

        if (success) {
            Toast.makeText(this, isEditing ? "Ingredient updated" : "Ingredient added",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save ingredient. Please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (storage != null) {
            storage.close();
        }
    }
}
