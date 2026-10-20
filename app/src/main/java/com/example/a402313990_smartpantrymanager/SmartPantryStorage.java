package com.example.a402313990_smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SmartPantryStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 6;
    public static final String TABLE_INGREDIENTS = "pantry_ingredients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public SmartPantryStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Needed when deleting a recipe also deletes its ingredient rows.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        createIngredientsTable(db);
        RecipeCatalog.createAndSeed(db);
    }

    private void createIngredientsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INGREDIENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " REAL NOT NULL, " +
                COLUMN_UNIT + " TEXT NOT NULL, " +
                COLUMN_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_EXPIRY_DATE + " TEXT)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The pantry table holds the user's own data, so it is kept as it is.
        createIngredientsTable(db);

        // The recipe list is app-provided data; hence, it has to be rebuilt from scratch.
        // In this way, previous installations will have access to updated information on recipes and quantities.
        RecipeCatalog.dropTables(db);
        RecipeCatalog.createAndSeed(db);
    }

    public List<Recipe> getAllRecipes() {
        return RecipeCatalog.readAll(getReadableDatabase());
    }

    public Recipe getRecipeById(int recipeId) {
        return RecipeCatalog.readById(getReadableDatabase(), recipeId);
    }

    public Cursor getAllIngredients() {
        return getReadableDatabase().query(TABLE_INGREDIENTS, null, null, null, null, null,
                COLUMN_NAME + " COLLATE NOCASE ASC");
    }

    /** Each item in the pantry is returned as an object used in the recipe matcher. */
    public List<PantryIngredient> getAllPantryIngredients() {
        List<PantryIngredient> list = new ArrayList<>();

        try (Cursor cursor = getAllIngredients()) {
            int id = cursor.getColumnIndexOrThrow(COLUMN_ID);
            int name = cursor.getColumnIndexOrThrow(COLUMN_NAME);
            int quantity = cursor.getColumnIndexOrThrow(COLUMN_QUANTITY);
            int unit = cursor.getColumnIndexOrThrow(COLUMN_UNIT);
            int category = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY);
            int expiry = cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE);

            while (cursor.moveToNext()) {
                list.add(new PantryIngredient(
                        cursor.getInt(id),
                        cursor.getString(name),
                        cursor.getDouble(quantity),
                        cursor.getString(unit),
                        cursor.getString(category),
                        cursor.getString(expiry)));
            }
        }

        return list;
    }

    public Cursor getIngredient(int id) {
        return getReadableDatabase().query(TABLE_INGREDIENTS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
    }

    public long addIngredient(String name, double quantity, String unit, String category, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_EXPIRY_DATE, expiryDate == null ? "" : expiryDate);
        return getWritableDatabase().insert(TABLE_INGREDIENTS, null, values);
    }

    public int updateIngredient(int ingredientId, String name, double quantity, String unit,
                                String category, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_EXPIRY_DATE, expiryDate == null ? "" : expiryDate);
        return getWritableDatabase().update(TABLE_INGREDIENTS, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(ingredientId)});
    }

    public int deleteIngredient(int ingredientId) {
        return getWritableDatabase().delete(TABLE_INGREDIENTS, COLUMN_ID + "=?",
                new String[]{String.valueOf(ingredientId)});
    }

    public int deleteAllIngredients() {
        return getWritableDatabase().delete(TABLE_INGREDIENTS, null, null);
    }
}
