package edu.staybalanced.staybalanced;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gyroscope{


    protected Context context;

    private static ArrayList<Float> gyroVals = new ArrayList<Float>();
    //private float[] gyroVals = new float [0];// = new float[100];
    //private int index = 0;

    private static SensorEvent sensorEvent;

    private void getContext(Context context){
        this.context = context.getApplicationContext();
    }

    // rotation around x
    public static float getGyroX(){ return sensorEvent.values[0]; }

    // rotation around y axis
    public static float getGyroY(){ return sensorEvent.values[1]; }

    //rotation around z axis, same as left/right tilt if you're facing phone screen
    public static float getGyroZ(){
        return sensorEvent.values[2];
    }

    public void updateGyroVals(ArrayList<Float> newGyroVals){
        this.gyroVals = newGyroVals;
    }
    public ArrayList<Float> returnVals(){
        //float[] tmp = Arrays.copyOf(gyroVals);
        //this.gyroVals = new ArrayList<Float>();
        //gyroVals[index] = getGyroY();
        //Log.d("GYRO_Y",Float.toString(getGyroY()));
        Float z = new Float(getGyroZ());
        if(z>0.5f || z<-0.5f) {
            gyroVals.add(z);
        }
        //Log.d("GYRO_Y_F","done");
        //index++;
        return gyroVals;
    }

    public void storeValues(){
        /*
        get the gyroscope values we want
        write them to sql database
        */
    }

    public Gyroscope(SensorEvent sensorEvent){
        this.sensorEvent = sensorEvent;
        //this.gyroVals = new ArrayList<Float>();
    }

    public Gyroscope(SensorEvent sensorEvent, String exerciseName){
        /*
        Get the type of exercise they're doing
        process accordingly
        may want to build methods for each exercise in this class
        */
    }

}
