package edu.staybalanced.staybalanced;

import static java.util.Objects.isNull;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class History extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ExerciseItem> exercises;
    ExerciseAdapter exerciseAdapter;
    MenuItem menuItem;
    SearchView searchView;
    TextView txt_history;
    ImageView image_history;
    int graph_exercise_id;
    MutableLiveData<Integer> listener_graph_exercise_id;
    View thisView;
    LineChart chart;
    DatabaseHelper databaseHelper;
    LineDataSet timeInPositionDataSet;
    ArrayList<Long> dates_selected_exercise;
    LineDataSet lineData;
    ArrayList<ILineDataSet> dataSet;
    LineData chart_data;

    public History() {
        // Required empty public constructor
        super(R.layout.fragment_history);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(thisView.getContext()).unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.thisView = view;


        // Indicate the Fragment wants to contribute to the Host Activity's options menu
        setHasOptionsMenu(true);

        // initialize recyclerview
        recyclerView = view.findViewById(R.id.recycler_view);

        exercises = new ArrayList<>();

        // get database exercise items
        this.databaseHelper = new DatabaseHelper(view.getContext());
        exercises = databaseHelper.getAllExerciseItems();

        //adding default exercises if db is empty
        if (exercises.isEmpty()) {
            databaseHelper.addExercise(new Exercises(-1, "Wall Squat", 30, R.drawable.eicon_squat));
            databaseHelper.addExercise(new Exercises(-1, "Plank", 30, R.drawable.eicon_plank));
            databaseHelper.addExercise(new Exercises(-1, "Bicep Curl Hold", 30, R.drawable.eicon_b_curl));
            fillHistory(databaseHelper, 1);
            fillHistory(databaseHelper, 2);
            exercises = databaseHelper.getAllExerciseItems();
        }

        exerciseAdapter = new ExerciseAdapter(exercises, view.getContext(),"History");
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(exerciseAdapter);

        FloatingActionButton searchButton = view.findViewById(R.id.hist_fab);
        searchButton.setOnClickListener(fab -> {
            if (menuItem.isActionViewExpanded()) {
                menuItem.collapseActionView();
            }
            menuItem.expandActionView();
        });

        txt_history = view.findViewById(R.id.txt_history);
        txt_history.setText("Click on an Exercise below to see your progress");
        image_history = view.findViewById(R.id.image_history);

        // Line chart settings
        this.chart = view.findViewById(R.id.linechart);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.getDescription().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);

        chart.setVisibility(View.INVISIBLE);

        // Create a receiver for broadcast coming from exercise item click that draws the graph
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(receiver,
                new IntentFilter("clicked_exercise_id"));

        listener_graph_exercise_id = new MutableLiveData<>();

        listener_graph_exercise_id.setValue(0); // Initialize with a value

        listener_graph_exercise_id.observe(this.getViewLifecycleOwner(),new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (listener_graph_exercise_id.getValue() != null && listener_graph_exercise_id.getValue() != 0) {

                    ArrayList<Entry> yLabel = databaseHelper.getHistoryBarEntryData(graph_exercise_id);
                    if (yLabel.isEmpty()) {
                        Exercises curr_exercise = databaseHelper.getExerciseInfo(graph_exercise_id);
                        txt_history.setText("You haven't done this exercise yet: " + curr_exercise.getName() + "\nYou need to exercise to see your progress");
                        image_history.setVisibility(View.VISIBLE);
                        chart.setVisibility(View.INVISIBLE);

                    } else {
                        // get filler data
                        timeInPositionDataSet = new LineDataSet(yLabel, "Time in position");
                        dates_selected_exercise = databaseHelper.getHistoryDate(graph_exercise_id);
                        Exercises curr_exercise = databaseHelper.getExerciseInfo(graph_exercise_id);

                        xAxis.setValueFormatter(new DateAxisValueFormatter(dates_selected_exercise));
                        dataSet = new ArrayList<>();
                        dataSet.add(timeInPositionDataSet);

                        timeInPositionDataSet.setColor(getResources().getColor(R.color.orange));
                        timeInPositionDataSet.setLineWidth(4f);
                        timeInPositionDataSet.setValueTextSize(12f);

                        chart_data = new LineData(dataSet);

                        // put the data in the chart set it to visible and update it
                        chart.setVisibility(View.VISIBLE);
                        chart.setData(chart_data);
                        chart.invalidate();

                        txt_history.setText(curr_exercise.getName());
                        image_history.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

    }

    public static class DateAxisValueFormatter extends IndexAxisValueFormatter {
        private ArrayList<Long> dates_in;
        DateAxisValueFormatter(ArrayList<Long> dates) {
            this.dates_in = dates;
        }
        @Override
        public String getFormattedValue(float value) {
            try {
                int current_index = (int) value;
                long current_date = dates_in.get(current_index);
                Date itemDate = new Date(current_date);
                @SuppressLint("SimpleDateFormat") String itemDateStr = new SimpleDateFormat("MM/dd").format(itemDate);
                return itemDateStr;
            } catch (Exception e) {
                return "";
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // inflate the menu layout
        inflater.inflate(R.menu.menu_search, menu);

        super.onCreateOptionsMenu(menu, inflater);

        // set the menu item
        menuItem = menu.findItem(R.id.action_search);
        // get the action view from the menu item and set as a search view
        searchView = (SearchView) menuItem.getActionView();

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


    public void fillHistory(DatabaseHelper databaseHelper, int exerciseId) {
        // generate random data from today, yesterday, and the day before all under 30 secs in position
        databaseHelper.addExerciseHistory(new ExerciseHistory(-1, exerciseId, System.currentTimeMillis() - 24 * 60 * 60 * 1000 * 2, (int)(Math.random() * ((29 - 5) + 1)) + 5));
        databaseHelper.addExerciseHistory(new ExerciseHistory(-1, exerciseId, System.currentTimeMillis() - 24 * 60 * 60 * 1000, (int)(Math.random() * ((29 - 5) + 1)) + 5));
        databaseHelper.addExerciseHistory(new ExerciseHistory(-1, exerciseId, System.currentTimeMillis() , (int)(Math.random() * ((29 - 5) + 1)) + 5));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            graph_exercise_id = intent.getIntExtra("EXERCISE_ID", 0);
            listener_graph_exercise_id.setValue(graph_exercise_id);
        }
    };
}