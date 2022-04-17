package edu.staybalanced.staybalanced;

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
    ArrayList<ExcerciseItem> excercises;
    ExcerciseAdapter excerciseAdapter;

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

        excercises = new ArrayList<>();

        //TODO currently at the create step it makes the excercises, pull the excercise data
        //from the database and then add them to the excercises variable (List)
        // Also add a generic icon for any excercises we don't kno
        //adding placeholder excercises
        excercises.add(new ExcerciseItem("Wall Squat", R.drawable.eicon_squat));
        excercises.add(new ExcerciseItem("Plank", R.drawable.eicon_plank));
        excercises.add(new ExcerciseItem("Bicep Curl Hold", R.drawable.eicon_b_curl));

        for (int i = 0; i < 20; i ++){
            String nameE = "Example " + Integer.toString(i);
            excercises.add(new ExcerciseItem(nameE, R.drawable.eicon_plank));
        }

        excerciseAdapter = new ExcerciseAdapter(excercises,view.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(excerciseAdapter);
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
                excerciseAdapter.getFilter().filter(newText);
                // false if the SearchView should perform the default action of showing any suggestions if available
                // KEEP FALSE
                return false;
            }
        });
    }
}