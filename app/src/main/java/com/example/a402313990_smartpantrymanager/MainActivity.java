
package com.example.a402313990_smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fabAdd;
    private BottomNavigationView bottomNav;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);
        bottomNav = findViewById(R.id.bottomNav);
        textEmpty = findViewById(R.id.textEmpty);

        // Show the empty pantry message for now
        textEmpty.setVisibility(TextView.VISIBLE);

        // Handle the Add Item button
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(
                    MainActivity.this,
                    "Add ingredient screen coming next",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Handle bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_pantry) {
                Toast.makeText(
                        MainActivity.this,
                        "Pantry selected",
                        Toast.LENGTH_SHORT
                ).show();
                return true;

            } else if (itemId == R.id.nav_recipes) {
                Toast.makeText(
                        MainActivity.this,
                        "Recipes screen coming next",
                        Toast.LENGTH_SHORT
                ).show();
                return true;

            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(
                        MainActivity.this,
                        "Settings screen coming next",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }

            return false;
        });
    }
}