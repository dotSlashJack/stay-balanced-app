package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    NavController navController;
    View.OnClickListener homeButtonListener;
    private ViewPager2 image_slider;
    private SliderAdapter adapter_slider;
    private Handler handler = new Handler();

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

        image_slider = view.findViewById(R.id.home_images_rotation_view);
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());

        // create the assets if the db is empty
        if (databaseHelper.getAllAssets().isEmpty()) createUnlocksDefault(databaseHelper);

        List<UnlockedAssets> unlockedAssets = new ArrayList<>();
        unlockedAssets = databaseHelper.getAllAssets();

        // add assets to adapter
        adapter_slider = new SliderAdapter(unlockedAssets, image_slider);

        image_slider.setAdapter(adapter_slider);
        image_slider.setClipToPadding(false);
        image_slider.setClipChildren(false);
        image_slider.setOffscreenPageLimit(3);
        image_slider.getChildAt(0).setOverScrollMode(image_slider.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.16f);
            }
        });

        image_slider.setPageTransformer(transformer);

        image_slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });

    }

    // images auto scroll
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            image_slider.setCurrentItem(image_slider.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }


    private void createUnlocksDefault(DatabaseHelper databaseHelper) {
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.logo, "Looking good! Welcome", true));
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.reward_balance, "Just because you're trying", true));
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.reward_plank, "Looking stiff", false));
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.reward_plank_2, "Planking done right!", false));
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.reward_curl, "Looking curly", false));
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.reward_legs, "Stairs ain't got nothing on you", false));
        databaseHelper.addUnlockedAsset(
                new UnlockedAssets(R.drawable.reward_free, "Pro Athlete in the making!", false));
    }
}