package edu.staybalanced.staybalanced;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class Select extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ExerciseItem> individualExercises;
    ExerciseAdapter exerciseAdapter;

    public Select() {
        // Required empty public constructor
        super(R.layout.fragment_select);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Indicate the Fragment wants to contribute to the Host Activity's options menu
        setHasOptionsMenu(true);

        // initialize recyclerview
        recyclerView = view.findViewById(R.id.recycler_view);

        // get database exercise items
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
        individualExercises = databaseHelper.getAllExerciseItems();

        //adding default exercises if db is empty
        if (individualExercises.isEmpty()) {
            databaseHelper.addExercise(new Exercises(-1, "Wall Squat", 30, R.drawable.eicon_squat));
            databaseHelper.addExercise(new Exercises(-1, "Plank", 30, R.drawable.eicon_plank));
            databaseHelper.addExercise(new Exercises(-1, "Bicep Curl Hold", 30, R.drawable.eicon_b_curl));
            individualExercises = databaseHelper.getAllExerciseItems();
        }

        // put individual exercises into adapter
        exerciseAdapter = new ExerciseAdapter(individualExercises, view.getContext(), "Select");
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(exerciseAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // inflate the menu layout
        inflater.inflate(R.menu.menu_search, menu);

        super.onCreateOptionsMenu(menu, inflater);

        // set the menu item
        MenuItem item = menu.findItem(R.id.action_search);
        // get the action view from the menu item and set as a search view
        SearchView searchView = (SearchView) item.getActionView();

        // set the search filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                exerciseAdapter.getFilter().filter(newText);
                // false if the SearchView should perform the default action of showing any suggestions if available
                // KEEP FALSE
                return false;
            }
        });
    }
}