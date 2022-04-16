package edu.staybalanced.staybalanced;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.staybalanced.staybalanced.databinding.ActivityExerciseDoBinding;

/**
 * An example full-screen activity that shows and hides the Activity's controls UI
 * with user interaction.
 *
 * TODO: Instance saving not implemented.  Screen's state will be reset on orientation change.
 */
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
    private SensorManager sensorManager;
    private Sensor gyro;
    private SensorEventListener gyroListener;
    //private float[] gyroVals = new float[100];
    private ArrayList<Float> gyroVals;

    // Create a Handler to post delayed updates to the UI Thread from the Runnables defined below
    private final Handler mHideHandler = new Handler();

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
     * view.performClick() will call any onClickListeners that've been defined for the tapped View
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

        // Hide the default bar containing the Activity's name
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { actionBar.hide(); }

        binding = ActivityExerciseDoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BEGIN JACK'S GYROSCOPE CODE
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(gyro == null){
            Toast.makeText(this,"error in gyro", Toast.LENGTH_LONG).show();
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

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.fullscreenContent.setText("Dummy Button 1 Pressed");
            }
        });
        binding.dummyButton1.setOnTouchListener(mDelayHideTouchListener);
        binding.dummyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.fullscreenContent.setText("Dummy Button 2 Pressed");
            }
        });
        binding.dummyButton2.setOnTouchListener(mDelayHideTouchListener);
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
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroListener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView t = findViewById(R.id.fullscreen_content);
        //Toast toast = Toast.makeText(getApplicationContext(), "sddssd", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), (String)"okokok",
        //        Toast.LENGTH_LONG).show();
        /*if(sensorEvent.values[2] > 0.5f){
            //TextView t = findViewById(R.id.textView);
            //String gyro_reading = String.valueOf(sensorEvent.values[2]);
            gyroVals[index] = sensorEvent.values[2];
            index+=1;
            t.setText(Arrays.toString(gyroVals));
            //t.setText(gyro_reading);
            //t.setText()
        } else if(sensorEvent.values[2] < -.05f){

            gyroVals[index] = sensorEvent.values[2];
            index+=1;
            t.setText(Arrays.toString(gyroVals));
            //TextView t = findViewById(R.id.textView);
            String gyro_reading = String.valueOf(sensorEvent.values[2]);

            //t.setText(gyro_reading);
            //index+=1;
        }*/
        /*if(sensorEvent.values[2]>0.05f || sensorEvent.values[2]<-0.05f){
            gyroVals[index] = sensorEvent.values[2];
            index+=1;
            t.setText(Arrays.toString(gyroVals));
        }*/
        //t.setText("ssssss");
        Gyroscope g = new Gyroscope(sensorEvent);
        //g.updateGyroVals(gyroVals);
        gyroVals = g.returnVals();

        StringBuilder str = new StringBuilder();
        for (Float v : gyroVals) {
            str.append(v.toString());
            str.append(" ");
        }
        t.setText(str);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}