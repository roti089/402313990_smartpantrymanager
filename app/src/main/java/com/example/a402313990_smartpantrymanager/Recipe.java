package com.example.a402313990_smartpantrymanager;

import java.util.List;

/** A recipe containing its necessary ingredients and process. */
public class Recipe {
    private final int id;
    private final String name;
    private final String category;
    private final String description;
    private final int preparationMinutes;
    private final String difficulty;
    private final String instructions;
    private final List<RecipeIngredient> ingredients;

    public Recipe(int id, String name, String category, String description,
                  int preparationMinutes, String difficulty, String instructions,
                  List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.preparationMinutes = preparationMinutes;
        this.difficulty = difficulty;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getPreparationMinutes() { return preparationMinutes; }
    public String getDifficulty() { return difficulty; }
    public String getInstructions() { return instructions; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
