package edu.staybalanced.staybalanced;

import static java.lang.Math.abs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.staybalanced.staybalanced.databinding.ActivityExerciseDoBinding;

//An full-screen activity that shows and hides the Activity's controls UI with user interaction.
@SuppressWarnings("Convert2Lambda")
public class ExerciseDo extends AppCompatActivity implements SensorEventListener{

    /* Some older devices need a small delay between UI widget updates and a change of the status
     * and navigation bar.
     *
     * Changed this from Android Studio's default 300 to 0 because it made the device feel
     * unresponsive.
     */
    private static final int UI_ANIMATION_DELAY = 0;

    // Layout binding that allows us to reference Views without findViewById()
    private ActivityExerciseDoBinding binding;
    // Represents whether the controls are visible or not
    private boolean mVisible;
    // References the FrameLayout holding the Activity's content that will not be hidden
    private View unhiddenContent;
    // References the FrameLayout containing the Activity's controls.
    private View controlsContainer;

    // JACK'S GYROSCOPE ACTIVITY CLASS VARIABLES
    private SensorManager sensorManagerGyro;
    private SensorManager sensorManagerRotation;
    private Sensor gyro;
    private Sensor rotationVector;

    boolean isCalibrating;
    boolean isExercising;
    int exerciseId;
    boolean exerciseOnTrack;
    boolean runTimer;
    int seconds = 0;

    Gyroscope rotationObject;
    Gyroscope gyroObject;
    Gyroscope exerciseGyro;

    Hashtable<String, Float> rotationVals = new Hashtable<String, Float>();
    Hashtable<String, Float> gyroVals = new Hashtable<String, Float>();

    ArrayList<Boolean> exerciseTrackingList = new ArrayList<>();
    int secondsToRun;
    DatabaseHelper loadCalibrationHelper;
    Exercises current_exercise;
    MediaPlayer mediaPlayer;
    int currentlyPlaying;
    long previousWarning;
    boolean inPositionGyro = true;
    boolean inPositionRot= true;


    //TODO: need to check the implementation of the unregister listeners and see if these are needed
    private SensorEventListener gyroListener;
    private SensorEventListener rotationListener;

    // Create a Handler to post delayed updates to the UI Thread from the Runnables defined below
    private final Handler mHideHandler = new Handler();

    FloatingActionButton tutorialButton;
    private static int seq_counter;


    public boolean changeCalibration(){
        isCalibrating = !isCalibrating;
        return isCalibrating;
    }

    // An operation for a new Thread that will run after UI_ANIMATION_DELAY milliseconds, showing the
    // Activity's controls
    private final Runnable runnableShow = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            controlsContainer.setVisibility(View.VISIBLE);
        }
    };
    // An operation for a new Thread that will run after UI_ANIMATION_DELAY milliseconds, hiding the
    // Activity's controls
    private final Runnable runnableHide = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle intentArgs = getIntent().getExtras();
        exerciseId = intentArgs.getInt("EXERCISE_ID");
        Log.d("EXERCISE_DO", String.valueOf(exerciseId));

        runTimer = false;

        rotationVals = null;
        gyroVals = null;

        rotationObject = new Gyroscope("ROTATION_VECTOR"); // create gyroscope
        gyroObject = new Gyroscope("GYROSCOPE");

        exerciseGyro = new Gyroscope(exerciseId, getApplicationContext());

        // Hide the default bar containing the Activity's name
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { actionBar.hide(); }

        binding = ActivityExerciseDoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BEGIN JACK'S GYROSCOPE CODE
        sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManagerRotation = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyro = sensorManagerGyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotationVector = sensorManagerRotation.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Check if the device has a working gyroscope
        // TODO: gracefully degrade functionality if no gyro
        if(gyro == null){
            Toast.makeText(this,"error in gyro", Toast.LENGTH_LONG).show();
            finish();
        } else if(rotationVector == null){
            Toast.makeText(this,"error in rotation sensor", Toast.LENGTH_LONG).show();
            finish();
        }

        loadCalibrationHelper = new DatabaseHelper(getApplicationContext());
        current_exercise = loadCalibrationHelper.getExerciseInfo(exerciseId);
        secondsToRun = current_exercise.getSecondsPerRep();

        mVisible = true;
        unhiddenContent = binding.fullscreenContent;
        controlsContainer = binding.fullscreenContentControls;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // Resets currentlyPlaying var after audio completion
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                currentlyPlaying = -1;

            }
        });
        currentlyPlaying = -1;
        previousWarning = Instant.now().getEpochSecond();

        // Set up the user interaction to manually show or hide the system UI.
        unhiddenContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        isCalibrating = false;
        boolean didSave = false;
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //binding.fullscreenContent.setText("Dummy Button 1 Pressed");
                //binding.fullscreenContent.setText(Boolean.toString(isCalibrating));

                isCalibrating = !isCalibrating;
                if(!isCalibrating){
                    if(rotationVals!=null && gyroVals!=null){
                        binding.dummyButton1.setText("Calibrator");
                        binding.dummyButton2.setEnabled(true);
                        Gyroscope saveGyro = new Gyroscope(gyroVals, rotationVals, exerciseId);
                        boolean didSave = saveGyro.saveCalibration(getApplicationContext());
                        if(didSave!=true){
                            Toast toast = Toast.makeText(getApplicationContext(), "ERROR: calibration did NOT save correctly. Please try again.", Toast.LENGTH_LONG);
                            toast.show();
                        }else if(didSave){
                            //String printVals = "finished calibration, rotation values are: x "+Float.toString(rotationVals.get("rotation_x"))+" y "+Float.toString(rotationVals.get("rotation_y")) + " z "+Float.toString(rotationVals.get("rotation_z")) +"\n" + " gyro vals are: x "+Float.toString(gyroVals.get("gyro_x"))+ " y "+Float.toString(gyroVals.get("gyro_y")) + " z " + Float.toString(gyroVals.get("gyro_z"));
                            String printVals = "Calibration Complete, Press Excercise button to begin";
                            binding.fullscreenContent.setText(printVals);
                           // Toast toast = Toast.makeText(getApplicationContext(), "Calibration successfully saved!.", Toast.LENGTH_LONG);
                            //toast.show();
                            current_exercise = loadCalibrationHelper.getExerciseInfo(exerciseId);
                        }
                        exerciseGyro = new Gyroscope(exerciseId, getApplicationContext());
                        binding.dummyButton2.setEnabled(true);
                    }
                    else{
                        binding.fullscreenContent.setText("not calibrating, click btn 1 to starts");
                    }
                } else{
                    binding.dummyButton1.setText("Stop Calibrating");
                    binding.dummyButton2.setEnabled(false);
                }

                /*if(rotationVals!=null && isCalibrating==false){21
                    //Toast toast = Toast.makeText(getApplicationContext(), Float.toString(rotationVals.get("rotation_x")), Toast.LENGTH_LONG);
                    //toast.show();
                }*/

            }
        });

        binding.dummyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.fullscreenContent.setText("Dummy Button 2 Pressed");

                isExercising = !isExercising;
                if (isExercising && current_exercise.getRotationX() == 0 && current_exercise.getRotationY() == 0 && current_exercise.getRotationZ() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Calibrate, before starting excercise", Toast.LENGTH_LONG).show();
                    isExercising = false;
                    binding.dummyButton2.setEnabled(false);
                }
                else if(isExercising){
                    binding.dummyButton1.setEnabled(false);
                    binding.dummyButton2.setText("Stop exercise");
                    seconds = 0;
                    runTimer = true;
                    timer();
                }
                else if (isExercising == false) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    //TODO stop excercise early, stops audio
                    // reset button to say start excercise
                    // log seconds they did excercise
                    // Display results excercise to screen
                   /* DatabaseHelper exerciseSaver =  new DatabaseHelper(getApplicationContext());
                    ExerciseHistory current_exercise_history = new ExerciseHistory(-1, exerciseId, Instant.now().getEpochSecond(), seconds_in_pos);
                    //public Exercises(int id, String name, String description, int sets, int reps, int secondsPerRep, double gyroX, double gyroY, double gyroZ, double rotationX, double rotationY, double rotationZ, int image)
                    exerciseSaver.addExerciseHistory(current_exercise_history);*/


                }
            }
        });

        tutorialButton = findViewById(R.id.TutorialButton);

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = "Welcome to the Activity Tutorial!\n In order to start, please get in to " +
                        "position and then tap calibrate!";
                String s2 = "Once you are in position, tap calibrate again to complete calibration";
                String s3 = "Calibration Completed!\n Tap the start excercise button to start timer " +
                        "and begin the excercise!";

                /* The code below runs the animation for the tutorial and manages what occurs
                while the user progresses to the tutorial. The progression relies on the
                seq_counter variable
                 */

                new TapTargetSequence(ExerciseDo.this)
                        .targets(
                                TapTarget.forView(binding.dummyButton1,"Tutorial Part 1", s1)
                                        .outerCircleColor(android.R.color.holo_orange_dark)      // Specify a color for the outer circle
                                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                        .titleTextSize(28)                  // Specify the size (in sp) of the title text
                                        .titleTextColor(R.color.white)      // Specify the color of the title text
                                        .descriptionTextSize(24)            // Specify the size (in sp) of the description text
                                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                        .textColor(R.color.white)            // Specify a color for both the title and description text
                                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                        .tintTarget(true)                   // Whether to tint the target view's color
                                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                        .targetRadius(60),
                                TapTarget.forView(binding.dummyButton1,"Tutorial Part 2",s2)
                                        .outerCircleColor(android.R.color.holo_orange_dark)      // Specify a color for the outer circle
                                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                        .titleTextSize(28)                  // Specify the size (in sp) of the title text
                                        .titleTextColor(R.color.white)      // Specify the color of the title text
                                        .descriptionTextSize(24)            // Specify the size (in sp) of the description text
                                        .descriptionTextColor(R.color.orange)  // Specify the color of the description text
                                        .textColor(R.color.white)            // Specify a color for both the title and description text
                                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                        .tintTarget(true)                   // Whether to tint the target view's color
                                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                        .targetRadius(60),
                        TapTarget.forView(binding.dummyButton2,"Tutorial Final Part",s3)
                                .outerCircleColor(android.R.color.holo_orange_dark)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(28)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(24)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.orange)  // Specify the color of the description text
                                .textColor(R.color.white)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60))
                        .listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                // This will run at the end of the sequence
                                // it resets the sequence counter
                                // and begins the excercise
                                ExerciseDo.seq_counter = 0;
                                binding.dummyButton2.performClick();
                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                                // This method determines the action that will occur
                                // during the tutorial sequence based on the
                                // seq_counter var
                                if (ExerciseDo.seq_counter == 0) {
                                    binding.dummyButton1.performClick();
                                }
                                else if (ExerciseDo.seq_counter == 1) {
                                    binding.dummyButton1.performClick();
                                }
                                ExerciseDo.seq_counter += 1;


                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                // resets the counter when the sequence is cancelled at any point
                                ExerciseDo.seq_counter = 0;
                            }
                        }).start();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /* Trigger the initial hide() shortly after the activity has been created to briefly hint to
         * the user that UI controls are available.
         */
        delayedHide(1000);
    }

    // Schedules a call to hide() in delay milliseconds, canceling any previously scheduled calls.
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(runnableHide);
        mHideHandler.postDelayed(runnableHide, delayMillis);
    }

    private void toggle() {
        if (mVisible) { hide(); }
        else { show(); }
    }

    private void hide() {
        // Hide UI first
        controlsContainer.setVisibility(View.GONE);
        mVisible = false;

        // Remove any pending Threads from the execution queue that are attempting to make the
        // controls visible
        mHideHandler.removeCallbacks(runnableShow);
    }

    private void show() {
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.postDelayed(runnableShow, UI_ANIMATION_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManagerGyro.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManagerRotation.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManagerGyro.unregisterListener(gyroListener);
        sensorManagerRotation.unregisterListener(rotationListener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //TextView t = findViewById(R.id.fullscreen_content);
        // if the user is calibrations
        if (isCalibrating) {
            /*if(gotRotation && gotGyro){
                isCalibrating = false;
            }*/
            // get rotation vector and sensor
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // collecting x,y,z data, if null collect current data
                rotationObject.updateEvent(sensorEvent, null);
                // keep null because we only want the last set recorded
                rotationVals = rotationObject.returnRotationVals();
            }

            else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroObject.updateEvent(sensorEvent, null);
                gyroVals = gyroObject.returnGyroVals();
                //gyroObject.saveCalibration(gyroVals.get("gyro_x"), gyroVals.get("gyro_y"),gyroVals.get("gyro_z"), "GYROSCOPE");
            }
        } else if(isExercising){
            if (seconds >= secondsToRun) {
                isExercising = false;
                int count = 0;
                for (Boolean b : exerciseTrackingList) {
                    if (b) count++;
                }
                binding.fullscreenContent.setText("Excercise Complete");
                double count_dbl = (double) count;
                double nEvents = (double) exerciseTrackingList.size();
                binding.dummyButton1.setEnabled(true);
                double seconds_dbl = (double) seconds;
                int seconds_in_pos = (int) (seconds_dbl * (count_dbl / nEvents));
                binding.fullscreenContent.setText("Exercised for " + String.valueOf(seconds)+" seconds \n with a total of " + String.valueOf(seconds_in_pos)+ " seconds in good form");

                if(exerciseTrackingList!=null && exerciseTrackingList.size() > 0){
                    count = 0;
                    for (Boolean b : exerciseTrackingList) {
                        if (b) count++;
                    }
                    count_dbl = (double) count;
                    nEvents = (double) exerciseTrackingList.size();

                    seconds_dbl = (double) seconds;
                    seconds_in_pos = (int) (seconds_dbl * (count_dbl / nEvents));
                    binding.fullscreenContent.setText("Exercised for " + String.valueOf(seconds)+" seconds \n with a total of " + String.valueOf(seconds_in_pos)+ " seconds in good form");
                    try{
                        DatabaseHelper exerciseSaver =  new DatabaseHelper(getApplicationContext());
                        ExerciseHistory current_exercise_history = new ExerciseHistory(-1, exerciseId, Instant.now().getEpochSecond(), seconds_in_pos);
                        //public Exercises(int id, String name, String description, int sets, int reps, int secondsPerRep, double gyroX, double gyroY, double gyroZ, double rotationX, double rotationY, double rotationZ, int image)
                        exerciseSaver.addExerciseHistory(current_exercise_history);

                        Toast toast = Toast.makeText(getApplicationContext(), "Exercise complete!.", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch(Exception e){
                        Toast toast = Toast.makeText(getApplicationContext(), "Error saving your exercise, it may not show up in history.", Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
                //binding.fullscreenContent.setText(String.valueOf(exerciseTrackingList));
                runTimer = false;
                exerciseTrackingList = new ArrayList<Boolean>();
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                exerciseGyro.updateEvent(sensorEvent, "ROTATION_VECTOR");
                exerciseOnTrack = exerciseGyro.exerciseTracker("ROTATION_VECTOR");
                exerciseTrackingList.add(exerciseOnTrack);
                if(exerciseOnTrack == false && Instant.now().getEpochSecond() - previousWarning > 3){
                    inPositionRot = false;
                    binding.fullscreenContent.setText("Fix your form!");
                    if (currentlyPlaying != UtilAudio.OFF_POSITION) {
                        mediaPlayer = UtilAudio.playNow(getApplicationContext(), mediaPlayer, UtilAudio.OFF_POSITION);
                        currentlyPlaying = UtilAudio.OFF_POSITION;
                        previousWarning = Instant.now().getEpochSecond();
                    }

                } else if(exerciseOnTrack == true && Instant.now().getEpochSecond() - previousWarning > 3) {
                    inPositionRot = true;
                    binding.fullscreenContent.setText("You're doing great!");
                    if (currentlyPlaying != UtilAudio.IN_POSITION) {
                        mediaPlayer = UtilAudio.playNow(getApplicationContext(), mediaPlayer, UtilAudio.IN_POSITION);
                        currentlyPlaying = UtilAudio.IN_POSITION;
                        previousWarning = Instant.now().getEpochSecond();
                    }
                }
            }

//            else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//                exerciseGyro.updateEvent(sensorEvent, "GYROSCOPE");
//                exerciseOnTrack = exerciseGyro.exerciseTracker("GYROSCOPE");
//                exerciseTrackingList.add(exerciseOnTrack);
//
//                if(exerciseOnTrack == false && Instant.now().getEpochSecond() - previousWarning > 3){
//                    inPositionGyro = true;
//                    binding.fullscreenContent.setText("outside gyro range!");
//                    if (currentlyPlaying != UtilAudio.OFF_POSITION) {
//                        mediaPlayer = UtilAudio.playNow(getApplicationContext(), mediaPlayer, UtilAudio.OFF_POSITION);
//                        currentlyPlaying = UtilAudio.OFF_POSITION;
//                        previousWarning = Instant.now().getEpochSecond();
//                    }
//
//                } else if(exerciseOnTrack == true && Instant.now().getEpochSecond() - previousWarning > 3){
//                    inPositionGyro = true;
//                    binding.fullscreenContent.setText("inside gyro range!");
//                    if (currentlyPlaying != UtilAudio.IN_POSITION) {
//                        mediaPlayer = UtilAudio.playNow(getApplicationContext(), mediaPlayer, UtilAudio.IN_POSITION);
//                        currentlyPlaying = UtilAudio.IN_POSITION;
//                        previousWarning = Instant.now().getEpochSecond();
//                    }
//                    //inPosition = true;
//                    // binding.fullscreenContent.setText("inside gyro range!");
//                }
//            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public void timer(){
        new Thread() {
            public void run() {
                while (runTimer) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seconds++;
                                if (secondsToRun - seconds == 7 && isExercising == true) {
                                    mediaPlayer = UtilAudio.playNow(getApplicationContext(),mediaPlayer,UtilAudio.FIVE_LEFT);
                                }

                                else if (secondsToRun - seconds == 5 && isExercising == true) {
                                    mediaPlayer = UtilAudio.playNow(getApplicationContext(),mediaPlayer,UtilAudio.COUNTDOWN);
                                }
                                else if (secondsToRun == seconds && isExercising == true) {
                                    mediaPlayer = UtilAudio.playLater(getApplicationContext(),mediaPlayer,UtilAudio.DONE);
                                    binding.dummyButton2.setText("START EXCERCISE");
                                }
                                //binding.fullscreenContent.setText("Time in exercise:\n"+String.valueOf(seconds)+" seconds");
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}