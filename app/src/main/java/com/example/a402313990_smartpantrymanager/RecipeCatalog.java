package com.example.a402313990_smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public final class RecipeCatalog {

    private RecipeCatalog() {
    }

    public static void createAndSeed(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL UNIQUE, " +
                        "category TEXT NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "prep_minutes INTEGER NOT NULL, " +
                        "difficulty TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)"
        );


        seedOne(db, "Tomato and Onion Omelette", "Breakfast",
                "A simple breakfast made with everyday ingredients",
                15, "Easy",
                "1. Chop the onion and tomato.\n" +
                        "2. Beat the eggs in a bowl.\n" +
                        "3. Fry the vegetables, add the eggs and cook until set.",
                ing("eggs", 2, "pieces", "onion", 50, "g",
                        "tomato", 100, "g"));

        seedOne(db, "Spaghetti with Tomato Sauce", "Lunch",
                "A simple pasta dish with tomato and onion",
                25, "Easy",
                "1. Cook the pasta according to the packet instructions.\n" +
                        "2. Chop the tomato and onion.\n" +
                        "3. Cook the vegetables until soft and mix with the pasta.",
                ing("pasta", 200, "g", "tomato", 150, "g",
                        "onion", 50, "g"));

        seedOne(db, "Peanut Butter Toast", "Breakfast",
                "A quick breakfast or snack",
                5, "Easy",
                "1. Toast the bread.\n" +
                        "2. Spread peanut butter over the bread.\n" +
                        "3. Serve immediately.",
                ing("bread", 2, "pieces", "peanut butter", 30, "g"));

        seedOne(db, "Chicken and Rice", "Dinner",
                "A filling everyday meal",
                35, "Easy",
                "1. Cook the rice.\n" +
                        "2. Season and cook the chicken thoroughly.\n" +
                        "3. Serve the chicken with the rice.",
                ing("chicken", 200, "g", "rice", 150, "g"));

        seedOne(db, "Chakalaka-Style Bean Stew", "Dinner",
                "A bean stew inspired by a South African favourite",
                25, "Easy",
                "1. Chop the onion and tomato.\n" +
                        "2. Fry the onion until soft.\n" +
                        "3. Add beans and tomato, then simmer until thick.\n" +
                        "4. Add chilli or curry powder if available.",
                ing("beans", 200, "g", "tomato", 100, "g",
                        "onion", 50, "g"));

        seedOne(db, "Banana and Yoghurt Bowl", "Snack",
                "A refreshing no-cook snack",
                5, "Easy",
                "1. Spoon yoghurt into a bowl.\n" +
                        "2. Slice the banana over the yoghurt.\n" +
                        "3. Serve chilled.",
                ing("banana", 1, "pieces", "yoghurt", 150, "g"));

        seedOne(db, "Chicken Sandwich", "Lunch",
                "A simple sandwich for lunch",
                15, "Easy",
                "1. Cook the chicken thoroughly.\n" +
                        "2. Slice the tomato.\n" +
                        "3. Place the chicken and tomato between two slices of bread.",
                ing("chicken", 100, "g", "bread", 2, "pieces",
                        "tomato", 50, "g"));

        seedOne(db, "Creamy Scrambled Eggs", "Breakfast",
                "Soft scrambled eggs with milk",
                10, "Easy",
                "1. Beat the eggs with the milk.\n" +
                        "2. Heat a little oil or butter in a pan.\n" +
                        "3. Stir the eggs gently until cooked through.",
                ing("eggs", 2, "pieces", "milk", 30, "ml"));

        seedOne(db, "Tomato and Bean Rice", "Lunch",
                "A budget-friendly rice and bean meal",
                25, "Easy",
                "1. Cook the rice.\n" +
                        "2. Chop the tomato and onion.\n" +
                        "3. Cook the vegetables with the beans.\n" +
                        "4. Serve the mixture over the rice.",
                ing("rice", 150, "g", "beans", 100, "g",
                        "tomato", 100, "g"));

        seedOne(db, "Potato Omelette", "Breakfast",
                "An easy omelette with potatoes",
                25, "Medium",
                "1. Peel and dice the potatoes and onion.\n" +
                        "2. Cook them in a pan until tender.\n" +
                        "3. Add beaten eggs and cook until set.",
                ing("potatoes", 200, "g", "eggs", 2, "pieces",
                        "onion", 30, "g"));

        seedOne(db, "Tuna Pasta", "Lunch",
                "A quick pasta meal with tinned tuna",
                20, "Easy",
                "1. Cook the pasta.\n" +
                        "2. Drain the tuna and chop the tomato.\n" +
                        "3. Warm the tuna and tomato in a pan.\n" +
                        "4. Mix with the pasta and serve.",
                ing("tuna", 100, "g", "pasta", 150, "g",
                        "tomato", 100, "g"));

        seedOne(db, "Apple and Cinnamon Oats", "Breakfast",
                "Warm oats with apple",
                12, "Easy",
                "1. Add oats and milk to a pot.\n" +
                        "2. Chop the apple and add it.\n" +
                        "3. Add cinnamon if available and simmer until soft.",
                ing("apple", 1, "pieces", "oats", 50, "g",
                        "milk", 150, "ml"));

        seedOne(db, "Chicken and Carrot Stir-Fry", "Dinner",
                "A simple stir-fry inspired by Asian cooking",
                25, "Medium",
                "1. Slice the chicken, carrots and onion.\n" +
                        "2. Cook the chicken thoroughly in a hot pan.\n" +
                        "3. Add the vegetables and cook until tender.",
                ing("chicken", 200, "g", "carrots", 100, "g",
                        "onion", 50, "g"));

        seedOne(db, "Lentil and Carrot Soup", "Lunch",
                "A warming vegetable and lentil soup",
                35, "Easy",
                "1. Chop the carrots and onion.\n" +
                        "2. Add lentils, vegetables and water to a pot.\n" +
                        "3. Simmer until the lentils and carrots are soft.",
                ing("lentils", 150, "g", "carrots", 100, "g",
                        "onion", 50, "g"));

        seedOne(db, "Cheese Omelette", "Breakfast",
                "Eggs folded around melted cheese",
                10, "Easy",
                "1. Beat the eggs.\n" +
                        "2. Pour into a lightly oiled pan.\n" +
                        "3. Add grated cheese and fold the omelette when the eggs begin to set.",
                ing("eggs", 2, "pieces", "cheese", 40, "g"));

        seedOne(db, "Banana Oat Yoghurt", "Breakfast",
                "A quick breakfast bowl with fruit and oats",
                5, "Easy",
                "1. Spoon yoghurt into a bowl.\n" +
                        "2. Add oats and sliced banana.\n" +
                        "3. Mix and serve.",
                ing("yoghurt", 150, "g", "oats", 40, "g",
                        "banana", 1, "pieces"));

        seedOne(db, "Rice and Beans", "Dinner",
                "A simple meal enjoyed in many countries",
                25, "Easy",
                "1. Cook the rice.\n" +
                        "2. Chop and soften the onion in a pan.\n" +
                        "3. Add the beans and heat through.\n" +
                        "4. Serve over the rice.",
                ing("rice", 150, "g", "beans", 100, "g",
                        "onion", 30, "g"));

        seedOne(db, "Tomato and Cheese Toast", "Snack",
                "Crispy toast with tomato and melted cheese",
                10, "Easy",
                "1. Slice the tomato and grate the cheese.\n" +
                        "2. Place both on the bread.\n" +
                        "3. Toast until the bread is crisp and the cheese melts.",
                ing("bread", 2, "pieces", "tomato", 100, "g",
                        "cheese", 50, "g"));

        seedOne(db, "Vegetable Soup", "Dinner",
                "A simple homemade soup",
                35, "Easy",
                "1. Chop the carrots, potatoes and onion.\n" +
                        "2. Simmer the vegetables in water until soft.\n" +
                        "3. Mash or blend to your preferred consistency.\n" +
                        "4. Season to taste and serve warm.",
                ing("carrots", 150, "g", "potatoes", 200, "g",
                        "onion", 40, "g"));

        seedOne(db, "Cereal with Milk", "Breakfast",
                "A quick breakfast before school or work",
                2, "Easy",
                "1. Pour cereal into a bowl.\n" +
                        "2. Add the milk.\n" +
                        "3. Serve immediately.",
                ing("cereal", 1, "packets", "milk", 150, "ml"));
    }

        private static RecipeIngredient[] ing(Object... data) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (int i = 0; i < data.length; i += 3) {
            ingredients.add(
                    new RecipeIngredient(
                            (String) data[i],
                            ((Number) data[i + 1]).doubleValue(),
                            (String) data[i + 2]
                    )
            );
        }

        return ingredients.toArray(new RecipeIngredient[0]);
    }

    private static void seedOne(
            SQLiteDatabase db,
            String name,
            String category,
            String description,
            int minutes,
            String difficulty,
            String instructions,
            RecipeIngredient[] ingredients
    ) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("category", category);
        recipeValues.put("description", description);
        recipeValues.put("prep_minutes", minutes);
        recipeValues.put("difficulty", difficulty);
        recipeValues.put("instructions", instructions);

        // Helps not to create duplicate recipes.
        long recipeId = db.insertWithOnConflict(
                "recipes",
                null,
                recipeValues,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        if (recipeId == -1) {
            try (Cursor cursor = db.query(
                    "recipes",
                    new String[]{"id"},
                    "name = ?",
                    new String[]{name},
                    null,
                    null,
                    null
            )) {
                if (cursor.moveToFirst()) {
                    recipeId = cursor.getLong(0);
                } else {
                    return;
                }
            }
        }

        // Add any ingredient to this recipe only if it lacks them.
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        )) {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        }

        for (RecipeIngredient ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("name", ingredient.getName());
            ingredientValues.put("quantity", ingredient.getQuantity());
            ingredientValues.put("unit", ingredient.getUnit());

            db.insert("recipe_ingredients", null, ingredientValues);
        }
    }

    /** Deletes the recipe tables, so that they can be recreated using the current seed data. */
    public static void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
    }

    /**Reads all recipes and their respective ingredients alphabetically arranged. */
    public static List<Recipe> readAll(SQLiteDatabase db) {
        List<Recipe> recipes = new ArrayList<>();

        try (Cursor cursor = db.query("recipes", null, null, null, null, null,
                "name COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                recipes.add(buildRecipe(db, cursor));
            }
        }

        return recipes;
    }

    /** Gets one recipe using its id, or gets null if it doesn't exist.*/
    public static Recipe readById(SQLiteDatabase db, int wantedId) {
        try (Cursor cursor = db.query("recipes", null, "id = ?",
                new String[]{String.valueOf(wantedId)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return buildRecipe(db, cursor);
            }
        }
        return null;
    }

    /** Creates a Recipe object based on the current row in the cursor and populates its ingredients.
     */
    private static Recipe buildRecipe(SQLiteDatabase db, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

        return new Recipe(
                id,
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getInt(cursor.getColumnIndexOrThrow("prep_minutes")),
                cursor.getString(cursor.getColumnIndexOrThrow("difficulty")),
                cursor.getString(cursor.getColumnIndexOrThrow("instructions")),
                readIngredients(db, id)
        );
    }

    /** Populates the ingredient rows of one recipe. */
    private static List<RecipeIngredient> readIngredients(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        try (Cursor cursor = db.query("recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(recipeId)}, null, null, "id ASC")) {
            while (cursor.moveToNext()) {
                ingredients.add(new RecipeIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }

        return ingredients;
    }
}
