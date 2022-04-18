package edu.staybalanced.staybalanced;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Gyroscope{


    protected Context context;

    private static ArrayList<Float> gyroValsX = new ArrayList<Float>();
    private static ArrayList<Float> gyroValsY = new ArrayList<Float>();
    private static ArrayList<Float> gyroValsZ = new ArrayList<Float>();

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

    private static boolean changeThreshold(Float gyroVal1, Float gyroVal2){
        //TODO: make this a better threshold
        if(abs(gyroVal1 - gyroVal2) > 0.05){
            return true;
        }
        else{
            return false;
        }
    }

    private static boolean checkSpeed(){
        //TODO: Implement this to make it check how fast the person is moving compared to their average during calibration or to range in calibration or something
        return true;
    }


    public void saveCalibration(){
        /*
        get the gyroscope values we want
        write them to sql database
        */
        //TODO: get the min and max values for each
        //TODO: pad them with margin of error
        //TODO: get the other 2 coordinate variables at that location, pad
        //TODO:
    }

    public void updateGyroVals(ArrayList<Float> newGyroValsX){
        //TODO: fully implement or remove
        this.gyroValsX = newGyroValsX;
    }

    //TODO: calcluate thresholds for change automatically based on range of values or average delta

    public ArrayList<Float> returnVals(){
        //get the gyro vals
        Float z = new Float(getGyroZ());
        Float x = new Float(getGyroX());
        Float y = new Float(getGyroY());

        //see if the values have changed enough to worry about
        if(gyroValsX.size() >= 1){
            if(changeThreshold(x, gyroValsX.get(gyroValsX.size()-1)) == true
                || changeThreshold(y, gyroValsY.get(gyroValsY.size()-1)) == true
                || changeThreshold(z, gyroValsZ.get(gyroValsZ.size()-1)) == true
            ){
                gyroValsX.add(x);
                gyroValsY.add(y);
                gyroValsZ.add(z);

                //TODO: delete these log statements when done with math
                ArrayList<Float> tmp = new ArrayList<Float>();
                tmp.add(x);
                tmp.add(y);
                tmp.add(x);
                Log.d("point", String.valueOf(tmp));
            }
        } else{
            gyroValsX.add(x);
            gyroValsY.add(y);
            gyroValsZ.add(z);
        }
        // else{ gyroValsY.add(y); }
        /*if(gyroValsY.size() > 1){
            if(changeThreshold(y, gyroValsY.get(gyroValsY.size()-1)) == true){
                gyroValsZ.add(y);
            }
        }// else{ gyroValsZ.add(z); }*/
        //Log.d("xVals", String.valueOf(gyroValsX));
        //Log.d("yVals", String.valueOf(gyroValsY));
        //Log.d("zVals", String.valueOf(gyroValsZ));

        return gyroValsX;
    }


    public Gyroscope(SensorEvent sensorEvent){
        this.sensorEvent = sensorEvent;
        //this.gyroVals = new ArrayList<Float>();
    }

    //TODO: 1. fit a line through the data
    //TODO: 2. feed it (x,y) (x,z), (y,z) etc. pairs and compare its results for the other coordinate to the actual values in the array list
    //TODO: 3. compare this to some threshold
    // A rudimentary approach might be to just compare the x, y, and z coordinates to those at the same index in the other file and see how far off things are
    //calculate the 3d distance between points
    private double distance3D(Float x1, Float y1, Float z1, Float x2, Float y2, Float z2){
        return sqrt(Math.pow((z2-z1),2) + Math.pow((y2-y1),2) + Math.pow((x2-x1),2));
    }

    // determine how much time has passed during exercise
    private double timer(){
        return 0.0;
    }

    private boolean exerciseTracker(ArrayList<Float> currX, ArrayList<Float> currY, ArrayList<Float> currZ, ArrayList<Float> exerciseX,  ArrayList<Float> exerciseY, ArrayList<Float> exerciseZ){
        Float moeX = 0.1F;
        Float moeY = 0.1F;
        Float moeZ = 0.1F;
        int xIndex = currX.size() - 1;
        int yIndex = currY.size() -1;
        int zIndex = currZ.size() -1;

        if(currX.get(xIndex) < exerciseX.get(xIndex)+moeX && currX.get(xIndex) > currX.get(xIndex)-moeX){
            if(currY.get(yIndex) < exerciseY.get(yIndex)+moeY && currY.get(yIndex) > currY.get(yIndex)-moeY){
                if(currZ.get(zIndex) < exerciseZ.get(zIndex)+moeZ && currZ.get(zIndex) > currZ.get(zIndex)-moeZ){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    private Hashtable<String, ArrayList<Float>> dummyCalibration(){
        Hashtable<String, ArrayList<Float>> dict = new Hashtable<String, ArrayList<Float>>();
        ArrayList<Float> dummyXList = new ArrayList<Float>();
        ArrayList<Float> dummyYList = new ArrayList<Float>();
        /*for(int i; i<n; i++){

        }*/
        return dict;
    }

    public Gyroscope(SensorEvent sensorEvent, String exerciseName){
        /*
        Get the type of exercise they're doing
        process accordingly
        may want to build methods for each exercise in this class
        */
    }

}
