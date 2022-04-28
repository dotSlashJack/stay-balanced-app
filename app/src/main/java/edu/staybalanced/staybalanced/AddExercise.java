package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//TODO Disable reps and sets

public class AddExercise extends Fragment {
    AutoCompleteTextView name;
    EditText desc, reps, sets, secs;
    // Icon RecyclerView Class Variables
    List<IconItem> itemList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerLayoutManager;
    RecyclerView.Adapter recyclerAdapter;
    ImageView icon;
    TextView iconName;
    NavController navController;

    public AddExercise() {
        // Required empty public constructor
        super(R.layout.fragment_add_exercise);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        name = view.findViewById(R.id.add_name);
        desc = view.findViewById(R.id.add_desc);
        reps = view.findViewById(R.id.add_reps);
        sets = view.findViewById(R.id.add_sets);
        secs = view.findViewById(R.id.add_secs);

        // Set autocomplete example exercise names
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
        ArrayList<String> exampleNames = databaseHelper.getAllExerciseNames();
        ArrayAdapter<String> nameAutocomplete = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, exampleNames);
        name.setAdapter(nameAutocomplete);

        // Set up Icon recyclerview
        recyclerView = view.findViewById(R.id.add_icon_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new GridLayoutManager(view.getContext(), 4);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        setIconChoices();
        icon = view.findViewById(R.id.add_selected_icon);
        iconName = view.findViewById(R.id.add_icon_name);
        recyclerAdapter = new IconAdapter(itemList, icon, iconName);
        recyclerView.setAdapter(recyclerAdapter);

        Button add = view.findViewById(R.id.add_button);
        add.setOnClickListener(addButton -> {
            Exercises new_exercise;
            int iconId;
            try {
                if (iconName.getText() == null) {
                    iconId = R.drawable.eicon_b_curl;
                } else {
                    iconId = Integer.parseInt(iconName.getText().toString());
                }

                new_exercise = new Exercises(-1,
                        name.getText().toString(),
                        desc.getText().toString(),
                        Integer.parseInt(sets.getText().toString()),
                        Integer.parseInt(reps.getText().toString()),
                        Integer.parseInt(secs.getText().toString()),
                        iconId

                );
                Toast.makeText(view.getContext(), "New exercise " + name.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                navController.navigate(NavGraph00Directions.toSelect());
            }
            catch (Exception e) {
                Toast.makeText(view.getContext(), "Error adding exercise", Toast.LENGTH_SHORT).show();
                new_exercise = new Exercises(-1, "error", "error while adding exercise", 0, 0, 0, 0);
            }
            boolean success = databaseHelper.addExercise(new_exercise);

            //Toast.makeText(view.getContext(), "Success = " + success, Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // When the Fragment is in view, automatically focus on the name field and bring up the
        // keyboard to allow seamless user interaction.
        // Source: https://stackoverflow.com/a/39144104/13084818
        name.requestFocus();
        name.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
        }, 150);
    }

    private void setIconChoices() {
        // Searches for all drawable resources beginning with "eicon_" using Java Reflection and
        // adds them to itemList for display in a RecyclerView
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            if (f.getName().startsWith("eicon_")) {
                try {
                    itemList.add(new IconItem (f.getInt(R.drawable.class), f.getName()));
                }
                catch (IllegalAccessException ignored) {}
            }
        }
    }
}
