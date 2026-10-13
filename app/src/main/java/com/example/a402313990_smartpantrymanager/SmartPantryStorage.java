package com.example.a402313990_smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SmartPantryStorage extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

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

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_INGREDIENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL, "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_EXPIRY_DATE + " TEXT"
                + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database upgrade logic will be added when the schema changes.
    }

    public long addIngredient(
            String name,
            double quantity,
            String unit,
            String category,
            String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    public Cursor getAllIngredients() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }

    public int deleteIngredient(int ingredientId) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(ingredientId)}
        );
    }
}