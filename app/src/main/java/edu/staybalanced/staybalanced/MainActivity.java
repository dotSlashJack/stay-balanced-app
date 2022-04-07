package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        /* Steps for using Navigation to go to a different arrangement of Fragments (a Destination)
           rather than the FragmentManager that was used in onCreate() in previous commits

           1) Get a reference to the FragmentViewContainer's NavController
           2) Use SafeArgs' Classes that are created according to the NavGraph XML to select a
              navigation action.  At this time, I was unable to figure out how to determine which
              Fragment is currently on display, so I could not choose the correct action based on
              that.  Instead, I had to use a hacky try-catch syntax as a placeholder.  I presume
              that this will be replaced by more specific methods instead of 1 catch-all method when
              a proper BottomNavigationBar is implemented.
           3) Use the NavController to perform the action
         */
        NavController navController = Navigation.findNavController(this, R.id.frag_container);
        try {
            // First try to navigate from Fragment00 to Fragment01
            NavDirections action = Fragment00Directions.action00to01();
            navController.navigate(action);
        } catch (IllegalArgumentException e) {
            // If that fails, Fragment01 is currently in view.  Navigate to Fragment00 instead.
            Fragment01Directions.Action01to00 action = Fragment01Directions.action01to00();

            /* This code is extra and only used to showcase how SafeArgs' generated Classes have
             * setters that can be used to pass in arguments other than the default ones specified
             * in the NavGraph.
             *
             * Java 8+ provides the ThreadLocalRandom API to generate a stream of ints.  Here we use
             * it to generate a stream of 1 int from the range [0,3).  We extract the random int
             * using the Stream's iterator.  If it is true (1) then we demonstrate using a SafeArgs
             * setter; otherwise, we use the Navigation Action's default value defined in the
             * NavGraph.
             *
             * Reference: https://www.baeldung.com/java-generating-random-numbers#3-javautilconcurrentthreadlocalrandom
             */
            IntStream randomizer = ThreadLocalRandom.current().ints(1, 0, 3);
            int i = randomizer.iterator().nextInt();
            Log.d("Randomizer", String.valueOf(i));
            if (i == 1) { action.setNewMsg("Main's Default Message"); }

            navController.navigate(action);
        }
    }
}