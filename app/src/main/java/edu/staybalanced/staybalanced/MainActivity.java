package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* This check for a savedInstanceState prevents the Fragment from being instantiated when it
           already has been instantiated.  If the Activity's configuration changes and onCreate() is
           called again, the Fragment is automatically restored from the savedInstanceState.
         */
        if (savedInstanceState == null) {
            /* Example of using a FragmentManager to programmatically swap in a Fragment in a
               FragmentContainerView

               .setReorderingAllowed() is usually always set to true for a FragmentTransaction
               .add(): A Bundle of values can be passed as the 3rd argument to help instantiate the
                  Fragment Class named in the 2nd argument.  This bundle can be unpacked in the
                  Fragment's onCreate() method.
                * .replace() is an alternative to .add() which, in essence, first calls .remove() on
                  all Fragments currently in a FragmentContainerView before calling .add().
                * .add() and .replace() also have a 4th argument: String _tag_  -- This argument is
                  used to programmatically assign a Tag to a Fragment (the equivalent of naming such
                  a tag in the Fragment's XML using "android: tag='' ") which can be used to obtain
                  a reference to the Fragment with findFragmentByTag().
                   * Alternative to this, you can find a Fragment by its ID:
                     fragmentManager.findFragmentById(_id-of-the-Fragment-Container-View);
               .addToBackStack(): This method adds all the Fragments instantiated as part of this
                  transaction to the Back Stack.  The user can then remove these Fragments by
                  pressing the Back button.
                * A String _name_ argument can be given to this method to allow us to go back to a
                  specific Back stack state by referring to its name.
                * AppCompatActivity.getSupportFragmentManager().popBackStack() can be called to
                  programmatically "press" the Back button.
                * If you call .remove() on a Fragment and don't also call .addToBackStack() on the
                  FragmentTransaction, then the Fragment is Destroyed.  If you do, then the Fragment
                  is only Stopped and can later be Resumed.
             */
            Bundle fragArgs = new Bundle();
            fragArgs.putString("a_string", "A String from MainActivity");

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.frag_container, Fragment00.class, fragArgs)
                    .addToBackStack(null)
                    .commit();
        }


    }

    public void onClick(View view) {
        /* Steps for using Navigation to go to a different arrangement of Fragments (a Destination)
           rather than teh FragmentManager

           1) We must get a reference to the NavHost in the FragmentContainerView.
           2) Using SafeArg's generated Classes and methods, we generate an Action based on the
              Actions defined in our NavGraph XML.
           3) We get a reference to the NavHost's NavController object
           4) Using the NavController, we execute the Navigation Action
         */
        NavHostFragment navHostFrag = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.frag_container);
        NavDirections action = Fragment00Directions.actionFragment00ToPlaceholder();
        NavController navController = navHostFrag.getNavController();
        navController.navigate(action);
    }
}