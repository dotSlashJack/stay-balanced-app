package edu.staybalanced.staybalanced;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Fragment01 extends Fragment {

    public Fragment01() { super(R.layout.fragment_01); }

    /* This Class was generated with Android Studio's "scrollable fragment" template.  Unsure why
     * this causes it to make this method nullable.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_01, container, false);
    }
}