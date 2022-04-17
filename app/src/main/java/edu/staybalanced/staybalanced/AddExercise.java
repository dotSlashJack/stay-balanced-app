package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class AddExercise extends Fragment {
    //TODO icon selector activity, if we have time
    AutoCompleteTextView name;

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

        // TODO: Replace list with array of names retrieved from database
        String[] exampleNameList = new String[] { "Plank", "Wall Sits" };
        ArrayAdapter<String> nameAutocomplete = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, exampleNameList);
        name = view.findViewById(R.id.add_name);
        name.setAdapter(nameAutocomplete);

        Button add = view.findViewById(R.id.add_button);
        add.setOnClickListener(addButton -> {
            // TODO: Write input to database
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
}
