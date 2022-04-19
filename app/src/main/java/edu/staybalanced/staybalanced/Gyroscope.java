package edu.staybalanced.staybalanced;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class Gyroscope{


    //protected Context context;

    private static ArrayList<Float> gyroValsX = new ArrayList<Float>();
    private static ArrayList<Float> gyroValsY = new ArrayList<Float>();
    private static ArrayList<Float> gyroValsZ = new ArrayList<Float>();

    private static ArrayList<Float> rotationValsX = new ArrayList<Float>();
    private static ArrayList<Float> rotationValsY = new ArrayList<Float>();
    private static ArrayList<Float> rotationValsZ = new ArrayList<Float>();

    //private static SensorEvent sensorEvent;
    private static SensorEvent gyroSensor;
    private static SensorEvent rotationSensor;

    // rotation around x

    public static float getGyroX(){ return gyroSensor.values[0]; }

    // rotation around y axis
    public static float getGyroY(){ return gyroSensor.values[1]; }

    //rotation around z axis, same as left/right tilt if you're facing phone screen
    public static float getGyroZ(){
        return gyroSensor.values[2];
    }

    // rotation angle around x
    public static float getRotationX(){ return rotationSensor.values[0]; }

    // rotation angle around y axis
    public static float getRotationY(){ return rotationSensor.values[1]; }

    //rotation angle round z axis
    public static float getRotationZ(){
        return rotationSensor.values[2];
    }

    private static boolean changeThreshold(Float gyroVal1, Float gyroVal2){
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

    private static Hashtable<String, Float> getInflectionPoints(ArrayList<Float> xVals, ArrayList<Float> yVals, ArrayList<Float> zVals){
        Hashtable<String, Float> inflections = new Hashtable<String, Float>();

        ArrayList<Float> xValsSorted = xVals;
        ArrayList<Float> yValsSorted = yVals;
        ArrayList<Float> zValsSorted = zVals;
        Collections.sort(xValsSorted);
        Collections.sort(yValsSorted);
        Collections.sort(zValsSorted);

        inflections.put("x_min", xValsSorted.get(0));
        inflections.put("x_max", xValsSorted.get(xValsSorted.size()-1));
        inflections.put("y_min", yValsSorted.get(0));
        inflections.put("y_max", yValsSorted.get(yValsSorted.size()-1));
        inflections.put("z_min", zValsSorted.get(0));
        inflections.put("z_max", zValsSorted.get(zValsSorted.size()-1));

        return inflections;
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

    public ArrayList<Float> returnGyroVals(){
        //get the gyro vals
        Float gyroZ = new Float(getGyroZ());
        Float gyroX = new Float(getGyroX());
        Float gyroY = new Float(getGyroY());

        //see if the values have changed enough to worry about
        if(gyroValsX.size() >= 1){
            if(changeThreshold(gyroX, gyroValsX.get(gyroValsX.size()-1)) == true
                || changeThreshold(gyroY, gyroValsY.get(gyroValsY.size()-1)) == true
                || changeThreshold(gyroZ, gyroValsZ.get(gyroValsZ.size()-1)) == true
            ){
                gyroValsX.add(gyroX);
                gyroValsY.add(gyroY);
                gyroValsZ.add(gyroZ);

                //rotationValsX.add(rotationX);

                //TODO: delete these log statements when done with math
                ArrayList<Float> tmp = new ArrayList<Float>();
                tmp.add(gyroX);
                tmp.add(gyroY);
                tmp.add(gyroZ);
                Log.d("gyro", String.valueOf(tmp));

                //ArrayList<Float> tmp2 = new ArrayList<Float>();
                //Log.d("rot", String.valueOf(tmp));
            }
        } else{
            gyroValsX.add(gyroX);
            gyroValsY.add(gyroY);
            gyroValsZ.add(gyroZ);
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

    public ArrayList<Float> returnRotationVals(){
        //get the gyro vals
        Float rotationX = new Float(getRotationX());
        Float rotationY = new Float(getRotationY());
        Float rotationZ = new Float(getRotationZ());

        //see if the values have changed enough to worry about
        if(rotationValsX.size() >= 1){
            if(changeThreshold(rotationX, gyroValsX.get(gyroValsX.size()-1)) == true
                    || changeThreshold(rotationY, gyroValsY.get(gyroValsY.size()-1)) == true
                    || changeThreshold(rotationZ, gyroValsZ.get(gyroValsZ.size()-1)) == true
            ){
                rotationValsX.add(rotationX);
                rotationValsY.add(rotationY);
                rotationValsY.add(rotationZ);

                //rotationValsX.add(rotationX);

                //TODO: delete these log statements when done with math
                ArrayList<Float> tmp = new ArrayList<Float>();
                tmp.add(rotationX);
                tmp.add(rotationY);
                tmp.add(rotationZ);
                Log.d("rotation", String.valueOf(tmp));

            }
        } else{
            rotationValsX.add(rotationX);
            rotationValsY.add(rotationY);
            rotationValsZ.add(rotationZ);
        }

        return rotationValsX;
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
        int yIndex = currY.size() - 1;
        int zIndex = currZ.size() - 1;

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

    // constructor for exercsise after calibration
    //public Gyroscope(SensorEvent sensorEvent, String exerciseName){
        /*
        Get the type of exercise they're doing
        process accordingly
        may want to build methods for each exercise in this class
        */
    //}

    // constructor for initialization/calibration
    //public Gyroscope(SensorEvent gyroEvent, SensorEvent vectorEvent){
    public Gyroscope(SensorEvent event, String type){
        if(type=="GYROSCOPE"){
            this.gyroSensor = event;
        }
        else if(type=="ROTATION_VECTOR"){
            this.rotationSensor = event;
        }

        //this.gyroVals = new ArrayList<Float>();
    }

}
