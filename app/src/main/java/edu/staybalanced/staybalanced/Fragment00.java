package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment00 extends Fragment {

    public Fragment00() {
        // Required empty public constructor
        super(R.layout.fragment_00);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_00, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment00", Fragment00Args.fromBundle(getArguments()).getNewMsg());
        TextView text = view.findViewWithTag("text");
        text.setText(Fragment00Args.fromBundle(getArguments()).getNewMsg());
    }
}