package edu.staybalanced.staybalanced;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.staybalanced.staybalanced.databinding.ActivityExerciseDoBinding;

/**
 * An example full-screen activity that shows and hides the Activity's controls UI
 * with user interaction.
 *
 * TODO: Instance saving not implemented.  Screen's state will be reset on orientation change.
 */
@SuppressWarnings("Convert2Lambda")
public class ExerciseDo extends AppCompatActivity implements SensorEventListener{

    // Determines whether or not the controls should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
    private static final boolean AUTO_HIDE = true;
    // If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user control interaction before hiding the controls.
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /* Some older devices need a small delay between UI widget updates and a change of the status
     * and navigation bar.
     *
     * Changed this from Android Studio's default 300 to 0 because it made the device feel
     * unresponsive.
     */
    private static final int UI_ANIMATION_DELAY = 0; //300;

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

    //TODO: need to check the implementation of the unregister listeners and see if these are needed
    private SensorEventListener gyroListener;
    private SensorEventListener rotationListener;

    // Create a Handler to post delayed updates to the UI Thread from the Runnables defined below
    private final Handler mHideHandler = new Handler();

    public boolean changeCalibration(){
        isCalibrating = !isCalibrating;
        return isCalibrating;
    }

    /**
     * An unused Runnable.  This was used to hide the Android System's Notification bar (top of the
     * screen) and the Android System Navigation controls (bottom of the screen, Back|Home|History)
     *
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
     */

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

    /* Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     *
     * view.performClick() will call any onClickListeners that have been defined for the tapped View
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) { delayedHide(AUTO_HIDE_DELAY_MILLIS); }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
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
        //exerciseRotation = new Gyroscope(exerciseId);

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

        //TODO: this seems to work to catch at least a lack of gyroscope, but we may want to test this a bit more
        if(gyro == null){
            Toast.makeText(this,"error in gyro", Toast.LENGTH_LONG).show();
            finish();
        } else if(rotationVector == null){
            Toast.makeText(this,"error in rotation sensor", Toast.LENGTH_LONG).show();
            finish();
        }

        mVisible = true;
        unhiddenContent = binding.fullscreenContent;
        controlsContainer = binding.fullscreenContentControls;

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
                        String printVals = "finished calibration, rotation values are: x "+Float.toString(rotationVals.get("rotation_x"))+" y "+Float.toString(rotationVals.get("rotation_y")) + " z "+Float.toString(rotationVals.get("rotation_z")) +"\n" + " gyro vals are: x "+Float.toString(gyroVals.get("gyro_x"))+ " y "+Float.toString(gyroVals.get("gyro_y")) + " z " + Float.toString(gyroVals.get("gyro_z"));
                        binding.fullscreenContent.setText(printVals);
                        // TODO: 3rd argument to Gyroscope() will be ExerciseID // should be resolved
                        Gyroscope saveGyro = new Gyroscope(gyroVals, rotationVals, exerciseId);
                        boolean didSave = saveGyro.saveCalibration(getApplicationContext());
                        if(didSave!=true){
                            Toast toast = Toast.makeText(getApplicationContext(), "ERROR: calibration did NOT save correctly. Please try again.", Toast.LENGTH_LONG);
                            toast.show();
                        }else if(didSave){
                            Toast toast = Toast.makeText(getApplicationContext(), "Calibration successfully saved!.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                    else{
                        binding.fullscreenContent.setText("not calibrating, click btn 1 to starts");
                    }
                }

                /*if(rotationVals!=null && isCalibrating==false){21
                    //Toast toast = Toast.makeText(getApplicationContext(), Float.toString(rotationVals.get("rotation_x")), Toast.LENGTH_LONG);
                    //toast.show();
                }*/

            }
        });
        //binding.dummyButton1.setOnTouchListener(mDelayHideTouchListener); //TODO: if this needs to be working, then figure out a way to prevent it from resetting the boolean
        binding.dummyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.fullscreenContent.setText("Dummy Button 2 Pressed");
                /*if(exerciseOnTrack==true) {
                    //Toast toast = Toast.makeText(getApplicationContext(), Boolean.toString(exerciseOnTrack), Toast.LENGTH_SHORT);
                    //toast.show();
                    //binding.fullscreenContent.setText("within range");
                } else if(exerciseOnTrack == false){
                    binding.fullscreenContent.setText("outside range");
                    //Toast toast = Toast.makeText(getApplicationContext(), Boolean.toString(exerciseOnTrack), Toast.LENGTH_SHORT);
                    ///toast.show();
                }*/
                isExercising = !isExercising;

                if(isExercising){
                    seconds = 0;
                    runTimer = true;
                    //timer();
                } else{
                    runTimer = false;
                }
            }
        });
        //binding.dummyButton2.setOnTouchListener(mDelayHideTouchListener); //TODO: again here, do we need this implemented?
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
        //TODO: make sure we do the same for the rotation sensor
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
                // TODO set threshold
                rotationVals = rotationObject.returnRotationVals();
                //t.setText("rot");
                //TODO: call storage method
                //gotRotation = true;
            }

            else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroObject.updateEvent(sensorEvent, null);
                gyroVals = gyroObject.returnGyroVals();
                //gyroObject.saveCalibration(gyroVals.get("gyro_x"), gyroVals.get("gyro_y"),gyroVals.get("gyro_z"), "GYROSCOPE");
            }
        } else if(isExercising){
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                exerciseGyro.updateEvent(sensorEvent, "ROTATION_VECTOR");
                exerciseOnTrack = exerciseGyro.exerciseTracker("ROTATION_VECTOR");
                exerciseTrackingList.add(exerciseOnTrack);
                if(exerciseOnTrack == false){
                    binding.fullscreenContent.setText("outside rotation range");
                } else if(exerciseOnTrack==true){
                    binding.fullscreenContent.setText("inside rotation range!");
                }
            }

            else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                exerciseGyro.updateEvent(sensorEvent, "GYROSCOPE");
                exerciseOnTrack = exerciseGyro.exerciseTracker("GYROSCOPE");
                exerciseTrackingList.add(exerciseOnTrack);
                /*if(exerciseOnTrack == false){
                    binding.fullscreenContent.setText("outside gyro range");
                } else if(exerciseOnTrack==true){
                    binding.fullscreenContent.setText("inside gyro range!");
                }*/

            }

            //TODO: we'll get the number of true events divided by length of list to get %/time in good form
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void timer(){
        new Thread() {
            public void run() {
                while (runTimer) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                seconds++;
                                binding.fullscreenContent.setText(Integer.toString(seconds));
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