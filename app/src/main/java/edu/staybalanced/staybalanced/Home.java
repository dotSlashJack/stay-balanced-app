package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Home extends Fragment {


    NavController navController;
    View.OnClickListener homeButtonListener;

    public Home() {
        // Required empty public constructor
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        homeButtonListener = view1 -> {
            switch (view1.getId()) {
                case R.id.home_btnStartExercise:
                    navController.navigate(NavGraph00Directions.toSelect());
                    break;
                case R.id.home_btnAddExercise:
                    navController.navigate(NavGraph00Directions.toNewExercise());
            }
        };
        view.findViewById(R.id.home_btnStartExercise).setOnClickListener(homeButtonListener);
        view.findViewById(R.id.home_btnAddExercise).setOnClickListener(homeButtonListener);
    }
}