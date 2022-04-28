package edu.staybalanced.staybalanced;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Exercises table with columns in order
    private static final String EXERCISES_TABLE = "EXERCISES_TABLE";
    public static final String COLUMN_EXERCISE_ID = "EXERCISE_ID";
    public static final String COLUMN_EXERCISE_NAME = "EXERCISE_NAME";
    public static final String COLUMN_SECONDS_PER_REP = "SECONDS_PER_REP";
    public static final String COLUMN_GYRO_X = "GYRO_X";
    public static final String COLUMN_GYRO_Y = "GYRO_Y";
    public static final String COLUMN_GYRO_Z = "GYRO_Z";
    public static final String COLUMN_ROTATION_X = "ROTATION_X";
    public static final String COLUMN_ROTATION_Y = "ROTATION_Y";
    public static final String COLUMN_ROTATION_Z = "ROTATION_Z";
    public static final String COLUMN_IMAGE = "EXERCISE_IMAGE";

    // Exercise History table with columns in order
    private static final String HISTORY_TABLE = "EXERCISE_HISTORY_TABLE";
    public static final String COLUMN_HISTORY_INSTANCE_ID = "HISTORY_ID";
    public static final String COLUMN_HISTORY_EXERCISE_ID = "EXERCISE_ID";
    public static final String COLUMN_HISTORY_EPOCH_SECONDS = "EPOCH_SECONDS";
    public static final String COLUMN_HISTORY_SECONDS_IN_POSITION  = "SECONDS_IN_POSITION";

    // Unlocked Assets table with columns in order
    private static final String UNLOCKED_ASSETS_TABLE = "ASSETS_TABLE";
    public static final String COLUMN_ASSET_ID = "ASSET_ID";
    public static final String COLUMN_ASSET_FILENAME = "ASSET_FILENAME";
    public static final String COLUMN_ASSET_UNLOCKED = "ASSET_UNLOCKED";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "stay_balanced.db", null, 2);
    }

    // When we first try to access the database create appropriate tables if they don't exist
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createExercisesTable = "CREATE TABLE " + EXERCISES_TABLE + " ( " +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT, " +
                COLUMN_SECONDS_PER_REP + " INT, " +
                COLUMN_GYRO_X + " INT, " +
                COLUMN_GYRO_Y + " INT, " +
                COLUMN_GYRO_Z + " INT, " +
                COLUMN_ROTATION_X + " INT, " +
                COLUMN_ROTATION_Y + " INT, " +
                COLUMN_ROTATION_Z + " INT, " +
                COLUMN_IMAGE + " INT ) ";

        db.execSQL(createExercisesTable);

        String createExerciseHistoryTable = "CREATE TABLE " + HISTORY_TABLE + " ( " +
                COLUMN_HISTORY_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HISTORY_EXERCISE_ID + " INT, " +
                COLUMN_HISTORY_EPOCH_SECONDS + " INT, " +
                COLUMN_HISTORY_SECONDS_IN_POSITION + " INT ) ";

        db.execSQL(createExerciseHistoryTable);

        String createUnlockedAssetsTable = "CREATE TABLE " + UNLOCKED_ASSETS_TABLE + " (" +
                COLUMN_ASSET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ASSET_FILENAME + " TEXT, " +
                COLUMN_ASSET_UNLOCKED + " INT ) ";

        db.execSQL(createUnlockedAssetsTable);
    }

    // When different database versions exist and app is trying to access non-compliant database design
    // Helps with forward and backward compatibility
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        clearDb(db);
        onCreate(db);
    }

    // adds new record to exercises table
    public boolean addExercise (Exercises new_exercise) {

        // checks if passed exercise is has missing field
        if (new_exercise.getName().equals("error")) return false;

        // gets connection to database
        SQLiteDatabase db = this.getWritableDatabase();

        //clearDb();

        // creates row content values and adds in each row's content
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXERCISE_NAME, new_exercise.getName());
        cv.put(COLUMN_SECONDS_PER_REP, new_exercise.getSecondsPerRep());
        cv.put(COLUMN_GYRO_X, new_exercise.getGyroX());
        cv.put(COLUMN_GYRO_Y, new_exercise.getGyroY());
        cv.put(COLUMN_GYRO_Z, new_exercise.getGyroZ());
        cv.put(COLUMN_ROTATION_X, new_exercise.getGyroX());
        cv.put(COLUMN_ROTATION_Y, new_exercise.getGyroY());
        cv.put(COLUMN_ROTATION_Z, new_exercise.getGyroZ());
        cv.put(COLUMN_IMAGE, new_exercise.getImage());

        Log.d("sql app", "adding data " + new_exercise + " to " + EXERCISES_TABLE);

        // inserts content value to table and gets result if insert is successful
        long insert_result = db.insert(EXERCISES_TABLE, null, cv);

        // closes connection to database
        db.close();
        return (insert_result != -1);
    }

    // adds new record to exerciseHISTORY table
    public boolean addExerciseHistory (ExerciseHistory new_exercise_history) {

        // gets connection to database
        SQLiteDatabase db = this.getWritableDatabase();

        // creates row content values and adds in each row's content
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HISTORY_EXERCISE_ID, new_exercise_history.getExerciseId());
        cv.put(COLUMN_HISTORY_EPOCH_SECONDS, new_exercise_history.getEpochSeconds());
        cv.put(COLUMN_HISTORY_SECONDS_IN_POSITION, new_exercise_history.getSecondsInPosition());

        Log.d("sql app", "adding data " + new_exercise_history + " to " + HISTORY_TABLE);

        long insert_result = db.insert(HISTORY_TABLE, null, cv);

        // closes connection to database
        db.close();
        return (insert_result == 1);
    }

    // adds new record to unlockedAssets table
    public boolean addUnlockedAsset (UnlockedAssets new_asset) {

        // gets connection to database
        SQLiteDatabase db = this.getWritableDatabase();

        // creates row content values and adds in each row's content
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ASSET_FILENAME, new_asset.getResourceFilename());
        cv.put(COLUMN_ASSET_UNLOCKED, new_asset.getUnlocked());

        Log.d("sql app", "adding data " + new_asset + " to " + UNLOCKED_ASSETS_TABLE);

        long insert_result = db.insert(UNLOCKED_ASSETS_TABLE, null, cv);

        // closes connection to database
        db.close();
        return (insert_result == 1);
    }

    public void clearDb(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UNLOCKED_ASSETS_TABLE);
    }

    public ArrayList<ExerciseItem> getAllExerciseItems(){
        ArrayList<ExerciseItem> all_exercise_items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String getAllExerciseItemsQuery = "SELECT " + COLUMN_EXERCISE_ID + " , " + COLUMN_EXERCISE_NAME + " , " + COLUMN_IMAGE + " , " + COLUMN_SECONDS_PER_REP + " FROM " + EXERCISES_TABLE;
        Cursor cursor = db.rawQuery(getAllExerciseItemsQuery, null);

        // if the cursor contains at least one item
        if (cursor.moveToFirst()) {
            // loop through result set
            do {
                // get value of exercise at current cursor
                int ex_id = cursor.getInt(0);
                String ex_name = cursor.getString(1);
                int image = cursor.getInt(2);
                int secondsPerSet = cursor.getInt(3);
                ExerciseItem current_exercise_item = new ExerciseItem(ex_id, ex_name, image, secondsPerSet);
                all_exercise_items.add(current_exercise_item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return all_exercise_items;
    }

    public ArrayList<String> getAllExerciseNames(){

        ArrayList<String> all_exercise_names = new ArrayList<>();
        String getAllExercisesQuery = "SELECT " + COLUMN_EXERCISE_NAME + " FROM " + EXERCISES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getAllExercisesQuery, null);
        // if the cursor contains at least one item
        if (cursor.moveToFirst()) {
            // loop through result set
            do {
                all_exercise_names.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return all_exercise_names;
    }

    public Exercises getExerciseInfo(int exerciseID){
        String getExercise = "SELECT * FROM " + EXERCISES_TABLE + " WHERE " + COLUMN_EXERCISE_ID + " =? " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getExercise,new String[] {String.valueOf(exerciseID)});
        Exercises current_exercise = null;
        // if the cursor contains at least one item
        if (cursor.moveToFirst()) {
            // loop through result set
            do {
                int ex_id = cursor.getInt(0);
                String ex_name = cursor.getString(1);
                int ex_secs_per_rep = cursor.getInt(2);
                double ex_gyro_x = cursor.getDouble(3);
                double ex_gyro_y = cursor.getDouble(4);
                double ex_gyro_z = cursor.getDouble(5);
                double ex_rotation_x = cursor.getDouble(6);
                double ex_rotation_y = cursor.getDouble(7);
                double ex_rotation_z = cursor.getDouble(8);
                int ex_image = cursor.getInt(9);
                current_exercise = new Exercises(ex_id, ex_name, ex_secs_per_rep, ex_gyro_x, ex_gyro_y, ex_gyro_z, ex_rotation_x, ex_rotation_y, ex_rotation_z, ex_image);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return current_exercise;
    }

    public void setExerciseCalibration(int exercise_id, double gyro_x, double gyro_y, double gyro_z, double rotation_x, double rotation_y, double rotation_z) {
        Exercises current_exercise = getExerciseInfo(exercise_id);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXERCISE_NAME, current_exercise.getName());
        cv.put(COLUMN_SECONDS_PER_REP, current_exercise.getSecondsPerRep());
        cv.put(COLUMN_GYRO_X, gyro_x);
        cv.put(COLUMN_GYRO_Y, gyro_y);
        cv.put(COLUMN_GYRO_Z, gyro_z);
        cv.put(COLUMN_ROTATION_X, rotation_x);
        cv.put(COLUMN_ROTATION_Y, rotation_y);
        cv.put(COLUMN_ROTATION_Z, rotation_z);
        cv.put(COLUMN_IMAGE, current_exercise.getImage());

        db.update(EXERCISES_TABLE, cv, COLUMN_EXERCISE_ID + " = " + exercise_id, null);
        db.close();
    }

    public void updateSeconds(int exercise_id, int newSeconds) {
        Exercises current_exercise = getExerciseInfo(exercise_id);
        SQLiteDatabase db = this.getWritableDatabase();
        if (current_exercise.getSecondsPerRep() == newSeconds) return;
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXERCISE_NAME, current_exercise.getName());
        cv.put(COLUMN_EXERCISE_DESCRIPTION, current_exercise.getDescription());
        cv.put(COLUMN_EXERCISE_SETS, current_exercise.getSets());
        cv.put(COLUMN_EXERCISE_REPS, current_exercise.getReps());
        cv.put(COLUMN_SECONDS_PER_REP, newSeconds);
        cv.put(COLUMN_GYRO_X, current_exercise.getGyroX());
        cv.put(COLUMN_GYRO_Y, current_exercise.getGyroY());
        cv.put(COLUMN_GYRO_Z, current_exercise.getGyroZ());
        cv.put(COLUMN_ROTATION_X, current_exercise.getRotationX());
        cv.put(COLUMN_ROTATION_Y, current_exercise.getRotationY());
        cv.put(COLUMN_ROTATION_Z, current_exercise.getRotationZ());
        cv.put(COLUMN_IMAGE, current_exercise.getImage());

        db.update(EXERCISES_TABLE, cv, COLUMN_EXERCISE_ID + " = " + exercise_id, null);
        db.close();
    }

    public ArrayList<Integer> getHistoryDate(int exerciseID){
        String getExerciseHistory = "SELECT " + COLUMN_HISTORY_EPOCH_SECONDS + " FROM " + HISTORY_TABLE + " WHERE " + COLUMN_HISTORY_EXERCISE_ID + " = " + exerciseID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getExerciseHistory, null);

        ArrayList<Integer> times = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                int hist = cursor.getInt(0);
                times.add(hist);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return times;
    }

    public ArrayList<Integer> getHistoryTime(int exerciseID){
        String getExerciseHistory = "SELECT " + COLUMN_HISTORY_SECONDS_IN_POSITION + " FROM " + HISTORY_TABLE + " WHERE " + COLUMN_HISTORY_EXERCISE_ID + " = " + exerciseID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getExerciseHistory, null);

        ArrayList<Integer> times = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                int hist = cursor.getInt(0);
                times.add(hist);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return times;
    }

    public ArrayList<Entry> getHistoryBarEntryData(int exerciseID) {
        ArrayList<Entry> yLabel = new ArrayList<>();
        try {
            ArrayList<Integer> times = getHistoryTime(exerciseID);
            ArrayList<Integer> dates = getHistoryDate(exerciseID);

            for (int i = 0; i < times.size(); i++) {
                yLabel.add(new Entry(dates.get(i), times.get(i)));
            }
            return yLabel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        yLabel.add(new Entry(0, 0));
        return null;
    }
}