package edu.staybalanced.staybalanced;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String EXERCISES_TABLE = "EXERCISES_TABLE";
    public static final String COLUMN_EXERCISE_ID = "EXERCISE_ID";
    public static final String COLUMN_EXERCISE_NAME = "EXERCISE_NAME";
    public static final String COLUMN_EXERCISE_DESCRIPTION = "EXERCISE_DESCRIPTION";
    public static final String COLUMN_EXERCISE_SETS = "EXERCISE_SETS";
    public static final String COLUMN_EXERCISE_REPS = "EXERCISE_REPS";
    public static final String COLUMN_SECONDS_PER_REP = "SECONDS_PER_REP";
    public static final String COLUMN_GYROX = "GYROX";
    public static final String COLUMN_GYROY = "GYROY";
    public static final String COLUMN_GYROZ = "GYROZ";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "stay_balanced.db", null, 1);
    }

    // When we first try to access the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + EXERCISES_TABLE + " (" +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT, " +
                COLUMN_EXERCISE_DESCRIPTION + " TEXT, " +
                COLUMN_EXERCISE_SETS + " INT, " +
                COLUMN_EXERCISE_REPS + " INT, " +
                COLUMN_SECONDS_PER_REP + " INT, " +
                COLUMN_GYROX + " INT, " +
                COLUMN_GYROY + " INT, " +
                COLUMN_GYROZ + " INT ) ";

        db.execSQL(createTable);
    }

    // When different database versions exist and app is trying to access non-compliant database design
    // Helps with forward and backward compatibility
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    // adds new record to exercises table
    public boolean addExercise (Exercises new_exercise) {
        if (new_exercise.getName().equals("error")) return false;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXERCISE_NAME, new_exercise.getName());
        cv.put(COLUMN_EXERCISE_DESCRIPTION, new_exercise.getDescription());
        cv.put(COLUMN_EXERCISE_SETS, new_exercise.getSets());
        cv.put(COLUMN_EXERCISE_REPS, new_exercise.getReps());
        cv.put(COLUMN_SECONDS_PER_REP, new_exercise.getSecondsPerRep());
        cv.put(COLUMN_GYROX, new_exercise.getGyroX());
        cv.put(COLUMN_GYROY, new_exercise.getGyroY());
        cv.put(COLUMN_GYROZ, new_exercise.getGyroZ());

        Log.d("sql app", "adding data " + new_exercise.toString() + " to " + EXERCISES_TABLE);

        long insert_result = db.insert(EXERCISES_TABLE, null, cv);

        if (insert_result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<Exercises> getAllExercises(){

        List<Exercises> all_exercises = new ArrayList<>();

        String getAllExercisesQuery = "SELECT * FROM " + EXERCISES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getAllExercisesQuery, null);

        if (cursor.moveToFirst()) {
            // loop through result set
            do {
                // get value of exercise at current cursor
                int ex_id = cursor.getInt(0);
                String ex_name = cursor.getString(1);
                String ex_desc = cursor.getString(2);
                int ex_sets = cursor.getInt(3);
                int ex_reps = cursor.getInt(4);
                int ex_secs_per_rep = cursor.getInt(5);
                double ex_gyro_x = cursor.getDouble(6);
                double ex_gyro_y = cursor.getDouble(7);
                double ex_gyro_z = cursor.getDouble(8);

                // make it into a new Exercises instance and add it to final list
                Exercises current_exercise = new Exercises(ex_id, ex_name, ex_desc, ex_sets, ex_reps, ex_secs_per_rep, ex_gyro_x, ex_gyro_y, ex_gyro_z);
                all_exercises.add(current_exercise);

            } while (cursor.moveToNext());

        } else {
            // nothing was in the result set

        }

        cursor.close();
        db.close();

        return all_exercises;
    }

    public Boolean deleteExercise(Exercises exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + EXERCISES_TABLE + " WHERE " + COLUMN_EXERCISE_ID + " = " + exercise.getId();
        Cursor cursor = db.rawQuery(deleteQuery, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

}
