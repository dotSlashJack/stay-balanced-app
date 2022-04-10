package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class Recycler_view_searchable_activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Excercise> excercises;
    ExcerciseAdapter excerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_searchable);

        // initialize recyclerview
        recyclerView = findViewById(R.id.recycler_view);

        excercises = new ArrayList<>();

        //adding placeholder excercises
        excercises.add(new Excercise("Wall Squat", R.drawable.squat_icon));
        excercises.add(new Excercise("Plank", R.drawable.plank_icon));
        excercises.add(new Excercise("Bicep Curl Hold", R.drawable.b_curl_icon));

        for (int i = 0; i < 20; i ++){
            String nameE = "Example " + Integer.toString(i);
            excercises.add(new Excercise(nameE, R.drawable.plank_icon));
        }

        excerciseAdapter = new ExcerciseAdapter(excercises,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(excerciseAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // inflate the menu layout
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // set the menu item
        MenuItem item = menu.findItem(R.id.action_search);
        // get the action view from the menu item and set as a serach view
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
        return super.onCreateOptionsMenu(menu);

    }
}