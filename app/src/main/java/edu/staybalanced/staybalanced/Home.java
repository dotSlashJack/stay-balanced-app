package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Home extends Fragment {

    final NavController NAVIGATION = Navigation.findNavController(requireActivity(), R.id.frag_container);

    public Home() {
        // Required empty public constructor
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    View.OnClickListener homeButtonListener = view -> {
        switch (view.getId()) {
            case R.id.home_btnStartExercise:
                NAVIGATION.navigate(NavGraph00Directions.toGyro());
                break;
            case R.id.home_btnAddExercise:
                //NAVIGATION.navigate();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.home_btnStartExercise).setOnClickListener(homeButtonListener);
        view.findViewById(R.id.home_btnAddExercise).setOnClickListener(homeButtonListener);
    }
}