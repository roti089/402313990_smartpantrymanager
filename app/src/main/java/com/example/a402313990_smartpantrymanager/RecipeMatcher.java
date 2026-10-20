package com.example.a402313990_smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * App business logic core: THE STRICT-MATCHING RULE.
 *
 * Only those recipes are suggested that include all the ingredients in the pantry,
 * in at least required amounts. If any of the ingredients is not available or
 * there is less than the required amount, such recipes are left out.
 *
 * The matching has been designed to be lenient in regard to simple cases:
 *  - capitalization and plurals ("Tomatoes" = "tomato")
 *  - a few spelling variations ("yogurt" = "yoghurt")
 *  - units variation (1 kg = 150 g, 1 l = 300 ml)
 *  - pieces vs. grams for common fruits/vegetables (GRAMS_PER_PIECE)
 */
public final class RecipeMatcher {

    /** Allows to have rounding errors while making comparisons among decimals. */
    private static final double TOLERANCE = 0.000001;

    /** Approximated weight of each piece; applies only to the listed items. */
    private static final Map<String, Double> GRAMS_PER_PIECE = new HashMap<>();

    /** Other spelling variations that need to be treated as one ingredient. */
    private static final Map<String, String> NAME_ALIASES = new HashMap<>();

    static {
        GRAMS_PER_PIECE.put("tomato", 120.0);
        GRAMS_PER_PIECE.put("onion", 150.0);
        GRAMS_PER_PIECE.put("potato", 200.0);
        GRAMS_PER_PIECE.put("carrot", 70.0);
        GRAMS_PER_PIECE.put("apple", 180.0);
        GRAMS_PER_PIECE.put("banana", 120.0);

        NAME_ALIASES.put("yogurt", "yoghurt");
    }

    private RecipeMatcher() {
    }

    /** Provides only those recipes that can be prepared with the current pantry. */
    public static List<Recipe> findMatches(List<Recipe> recipes, List<PantryIngredient> pantry) {
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantry)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /** This is true if all the ingredients of the recipe are present in sufficient quantity. */
    public static boolean canMake(Recipe recipe, List<PantryIngredient> pantry) {
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return false;
        }
        // Strict rule: not a single ingredient may be missing or short
        return getMissingIngredients(recipe, pantry).isEmpty();
    }

    /**
     * It returns the list of ingredients in the recipe that the pantry lacks,
     * whether because of lack of availability or insufficient quantity.
     */
    public static List<RecipeIngredient> getMissingIngredients(Recipe recipe,
                                                               List<PantryIngredient> pantry) {
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (availableAmount(required, pantry) + TOLERANCE < required.getQuantity()) {
                missing.add(required);
            }
        }
        return missing;
    }

    /** When a recipe that is exactly one ingredient away from being makeable. */
    public static class AlmostThereMatch {
        public final Recipe recipe;
        public final RecipeIngredient missing;

        AlmostThereMatch(Recipe recipe, RecipeIngredient missing) {
            this.recipe = recipe;
            this.missing = missing;
        }
    }

    /**
     *BONUS OPTIONAL LIST: recipes that have all but one missing ingredient.
     * These can NEVER intersect with the results of findMatches(), since such recipes
     * will always have 0 missing ingredients.
     */
    public static List<AlmostThereMatch> findAlmostThere(List<Recipe> recipes,
                                                         List<PantryIngredient> pantry) {
        List<AlmostThereMatch> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
                continue;
            }
            List<RecipeIngredient> missing = getMissingIngredients(recipe, pantry);
            if (missing.size() == 1) {
                result.add(new AlmostThereMatch(recipe, missing.get(0)));
            }
        }
        return result;
    }

    /** Adds up everything in the pantry that counts as the required ingredient, in the recipe's unit. */
    private static double availableAmount(RecipeIngredient required, List<PantryIngredient> pantry) {
        String requiredName = normalize(required.getName());
        String requiredUnit = normalizeUnit(required.getUnit());

        double available = 0;
        for (PantryIngredient item : pantry) {
            if (!normalize(item.getName()).equals(requiredName)) {
                continue;
            }
            double converted = convert(requiredName, item.getQuantity(),
                    normalizeUnit(item.getUnit()), requiredUnit);
            if (converted >= 0) {
                available += converted;
            }
        }
        return available;
    }

    /** Lowers cases, trims, singularizes, and adds spelling alternatives to an ingredient name.*/
    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String name = value.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");

        if (name.endsWith("ies") && name.length() > 3) {
            name = name.substring(0, name.length() - 3) + "y";      // berries -> berry
        } else if (name.endsWith("oes") && name.length() > 3) {
            name = name.substring(0, name.length() - 2);            // tomatoes -> tomato
        } else if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 2) {
            name = name.substring(0, name.length() - 1);            // eggs -> egg
        }

        String alias = NAME_ALIASES.get(name);
        return alias != null ? alias : name;
    }

    /**
     * Converts the quantity of pantry items to the units used in the recipe.
     * Returns -1 if the units are not comparable, which is never considered to be a match.
     */
    private static double convert(String ingredientName, double quantity, String from, String to) {
        if (from.equals(to)) {
            return quantity;
        }

        // Weight
        if (from.equals("kg") && to.equals("g")) return quantity * 1000;
        if (from.equals("g") && to.equals("kg")) return quantity / 1000;

        // Volume
        if (from.equals("l") && to.equals("ml")) return quantity * 1000;
        if (from.equals("ml") && to.equals("l")) return quantity / 1000;

        // Pieces <-> weight, only for ingredients with a known average piece weight
        Double gramsPerPiece = GRAMS_PER_PIECE.get(ingredientName);
        if (gramsPerPiece != null) {
            if (from.equals("pieces") && to.equals("g")) return quantity * gramsPerPiece;
            if (from.equals("pieces") && to.equals("kg")) return quantity * gramsPerPiece / 1000;
            if (from.equals("g") && to.equals("pieces")) return quantity / gramsPerPiece;
            if (from.equals("kg") && to.equals("pieces")) return quantity * 1000 / gramsPerPiece;
        }

        return -1;
    }

    /** Converting variants of maps of units into one form. */
    private static String normalizeUnit(String value) {
        if (value == null) {
            return "";
        }

        String unit = value.toLowerCase(Locale.ROOT).trim();

        switch (unit) {
            case "gram": case "grams":
                return "g";
            case "kilogram": case "kilograms":
                return "kg";
            case "millilitre": case "millilitres": case "milliliter": case "milliliters":
                return "ml";
            case "litre": case "litres": case "liter": case "liters":
                return "l";
            case "piece": case "pieces": case "pc": case "pcs":
                return "pieces";
            case "packet": case "packets":
                return "packets";
            case "can": case "cans":
                return "cans";
            case "bottle": case "bottles":
                return "bottles";
            default:
                return unit;
        }
    }
}
