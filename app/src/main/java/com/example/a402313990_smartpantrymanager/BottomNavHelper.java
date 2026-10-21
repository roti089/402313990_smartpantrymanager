package com.example.a402313990_smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Common behavior of the bottom navigation for the three primary screens
 * (Pantry, Recipes, Settings) such that every screen has the same navigation functionality.
 */
public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    /**
     * Brings focus to the currently selected tab and opens all other tabs by Intent.
     *
     * @param currentItemId the menu id of the current screen
     */
    public static void setup(Activity activity, BottomNavigationView nav, int currentItemId) {
        // Selects the active tab before adding the listener to prevent it from navigating.
        nav.setSelectedItemId(currentItemId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentItemId) {
                return true; // Already on this screen
            }

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = RecipeSuggestionsActivity.class;
            } else if (id == R.id.nav_settings) {
                target = PantryPreferencesActivity.class;
            } else {
                return false;
            }

            Intent intent = new Intent(activity, target);
            if (id == R.id.nav_pantry) {
                //The pantry screen is the home screen; return to it instead of duplicating another one.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            activity.startActivity(intent);

            // Moving between recipes and settings replaces the screen instead of piling them up
            if (currentItemId != R.id.nav_pantry && id != R.id.nav_pantry) {
                activity.finish();
            }

            // false, leave the highlighted text as is because the new window highlights its own tab
            return false;
        });
    }
}
