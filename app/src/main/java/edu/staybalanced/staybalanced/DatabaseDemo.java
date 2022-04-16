package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class DatabaseDemo extends AppCompatActivity {

    private Button btn_add, btn_view;
    private EditText et_name, et_desc, et_sets, et_reps, et_secsPerRep;
    private ListView lv_exercises;
    private ArrayAdapter exercisesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_demo);

        btn_add = findViewById(R.id.add_ex);
        btn_view = findViewById(R.id.view_ex);
        et_name = findViewById(R.id.et_ex_name);
        et_desc = findViewById(R.id.et_ex_description);
        et_sets = findViewById(R.id.et_ex_sets);
        et_reps = findViewById(R.id.et_ex_reps);
        et_secsPerRep = findViewById(R.id.et_ex_secs_per_rep);
        lv_exercises = findViewById(R.id.lv_exercises);

        update_lv();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exercises new_exercise;
                try {
                    new_exercise = new Exercises(-1,
                            et_name.getText().toString(),
                            et_desc.getText().toString(),
                            Integer.parseInt(et_sets.getText().toString()),
                            Integer.parseInt(et_reps.getText().toString()),
                            Integer.parseInt(et_secsPerRep.getText().toString()),
                            // setting default values for gyro measurements
                            0.0, 0.0, 0.0
                            );
                    Toast.makeText(DatabaseDemo.this, "New exercise " + et_name.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(DatabaseDemo.this, "Error adding exercise", Toast.LENGTH_SHORT).show();
                    new_exercise = new Exercises(-1, "error", "error while adding exercise", 0, 0, 0, 0, 0, 0);
                }
                DatabaseHelper databaseHelper = new DatabaseHelper(DatabaseDemo.this);
                boolean success = databaseHelper.addExercise(new_exercise);

                Toast.makeText(DatabaseDemo.this, "Success = " + success, Toast.LENGTH_SHORT).show();

            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_lv();

            }
        });

        lv_exercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Exercises clickedExercise = (Exercises) adapterView.getItemAtPosition(position);
                DatabaseHelper databaseHelper = new DatabaseHelper(DatabaseDemo.this);
                databaseHelper.deleteExercise(clickedExercise);
                update_lv();
                Toast.makeText(DatabaseDemo.this, "Deleted Exercise ID: " + clickedExercise.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update_lv(){
        DatabaseHelper databaseHelper = new DatabaseHelper(DatabaseDemo.this);
        List<Exercises> all_exercises = databaseHelper.getAllExercises();

        exercisesArrayAdapter = new ArrayAdapter<Exercises>(DatabaseDemo.this, android.R.layout.simple_list_item_1, all_exercises);
        lv_exercises.setAdapter(exercisesArrayAdapter);
    }
}