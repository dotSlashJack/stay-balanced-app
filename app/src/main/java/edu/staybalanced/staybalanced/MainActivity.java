package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    NavigationBarView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the BottomNavigationView
        bottomMenu = findViewById(R.id.menu_view);
        /* Set the initially selected button.  This should correspond to the screen that's brought
         * up by default whenever this Activity is launched.  In this example, this default screen
         * is declared in the NavGraph as the startDestination.
         *
         * Note how the NavigationBarView Class handles highlighting the appropriate button as
         * selected whenever its Listener changes the Fragment.  If the Fragment is changed by a
         * different process (like an onClick() method) this automatic highlighting will not occur.
         * That other process should be sure to call .setSelectedItemId() where necessary.
         */
        bottomMenu.setSelectedItemId(R.id.menu_item_home);
        bottomMenu.setOnItemSelectedListener(item -> {
            NavController navController = Navigation.findNavController(findViewById(R.id.frag_container));
            switch(item.getItemId()) {
                case R.id.menu_item_settings:
                    // Navigate to Settings Fragment
                    navController.navigate(NavGraph00Directions.toSettings());
                    return true;
                case R.id.menu_item_home:
                    // Navigate to Home Fragment
                    navController.navigate(NavGraph00Directions.toHome());
                    return true;
                case R.id.menu_item_history:
                    // Navigate to History Fragment
                    navController.navigate(NavGraph00Directions.toHistory());
                    return true;
                default:
                    return false;
            }
        });
    }
}