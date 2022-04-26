package edu.staybalanced.staybalanced;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class Gyroscope{

    //protected Context context;

    // where we store the calibration values for each sensor until saving them
    Hashtable<String, Float> calibratedGyroVals = new Hashtable<String, Float>();
    Hashtable<String, Float> calibratedRotationVals = new Hashtable<String, Float>();

    // the actual sensor event to get value(s) from
    private static SensorEvent gyroSensor;
    private static SensorEvent rotationSensor;

    //private static Hashtable<String, ArrayList<Float>> exerciseGyroVals = new Hashtable<String, ArrayList<Float>>();
    //private static Hashtable<String, ArrayList<Float>> exercisRotationVals = new Hashtable<String, ArrayList<Float>>();

    //was it gyro or rotation and what the exercise is to look up
    String eventType;
    int exerciseID;

    // the x, y, and z values for the calibration (both gyro and rotation)
    // used for when exercising
    Float calibrationGyroX;
    Float calibrationGyroY;
    Float calibrationGyroZ;
    Float calibrationRotationX;
    Float calibrationRotationY;
    Float calibrationRotationZ;

    Hashtable<String, Float> marginsOfErrorGyro = new Hashtable<String, Float>();
    Hashtable<String, Float> marginsOfErrorRotation = new Hashtable<String, Float>();

    // constructor for calibrationt
    public Gyroscope(String eventType){
        this.eventType = eventType;
    }

    //constructor for *saving* the calibrated values
    //needed b/c we call inside onCreate method
    public Gyroscope(Hashtable<String, Float> calibratedGyroVals, Hashtable<String, Float> calibratedRotationVals, int exerciseID){
        this.calibratedGyroVals = calibratedGyroVals;
        this.calibratedRotationVals = calibratedRotationVals;
        this.exerciseID =  exerciseID;
    }

    //constructor for exercise
    public Gyroscope(int exerciseID, Context context){
        //this.eventType = eventType;
        this.exerciseID = exerciseID;
        //this.eventType = null;

        DatabaseHelper loadCalibrationHelper = new DatabaseHelper(context);
        Exercises current_exercise = loadCalibrationHelper.getExerciseInfo(exerciseID);
        // from the current_exercise you can get any calibration you need
        calibrationGyroX = (float) current_exercise.getGyroX();
        calibrationGyroY = (float) current_exercise.getGyroY();
        calibrationGyroZ = (float) current_exercise.getGyroZ();

        calibrationRotationX = (float) current_exercise.getRotationX();
        //Log.i("rotationVals", calibratedRotationVals.toString());
        calibrationRotationZ = (float) current_exercise.getRotationY();
        calibrationRotationZ = (float) current_exercise.getRotationZ();

        marginsOfErrorGyro = getMOE("GYROSCOPE");
        marginsOfErrorRotation = getMOE("ROTATION_VECTOR");
    }

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

    //see if a sensor's measurement changed enough to warrant logging and checking a new value
    private static boolean changeThreshold(Float val1, Float val2){
        if(abs(val1 - val2) > 0.01){ //TODO: check this value after several tests
            return true;
        }
        else{
            return false;
        }
    }

    //calculate the 3d distance between points
    /*private static Float distance3D(Float x1, Float y1, Float z1, Float x2, Float y2, Float z2){
        return (float) sqrt(Math.pow((z2-z1),2) + Math.pow((y2-y1),2) + Math.pow((x2-x1),2));
    }*/

    //returns true if calibration save succeeded
    //false if not
    public boolean saveCalibration(Context context){
        try{
            /*Float gyroXMOE = 0.1F;
            Float gyroYMOE = 0.1F;
            Float gyroZMOE = 0.1F;

            Float rotationXMOE = 0.1F;
            Float rotationYMOE = 0.1F;
            Float rotationZMOE = 0.1F;*/

            Float rotation_x = calibratedRotationVals.get("rotation_x");
            Float rotation_y = calibratedRotationVals.get("rotation_y");
            Float rotation_z = calibratedRotationVals.get("rotation_z");

            /*Float rotation_x_min = rotation_x - rotationXMOE;
            Float rotation_y_min = rotation_y - rotationXMOE;
            Float rotation_z_min = rotation_z - rotationZMOE;
            Float rotation_x_max = rotation_x + rotationXMOE;
            Float rotation_y_max = rotation_y + rotationYMOE;
            Float rotation_z_max = rotation_z + rotationZMOE;*/

            Float gyro_x = calibratedGyroVals.get("gyro_x");
            Float gyro_y = calibratedGyroVals.get("gyro_y");
            Float gyro_z = calibratedGyroVals.get("gyro_z");

            /*Float gyro_x_min = gyro_x - gyroXMOE;
            Float gyro_y_min = gyro_y - gyroYMOE;
            Float gyro_z_min = gyro_z - gyroZMOE;
            Float gyro_x_max = gyro_x + gyroXMOE;
            Float gyro_y_max = gyro_y + gyroZMOE;
            Float gyro_z_max = gyro_z + gyroZMOE;*/

            //TODO: we may want to make this save the min and max gyro vals instead of jsut the value itself
            DatabaseHelper calibrationSaver = new DatabaseHelper(context);
            calibrationSaver.setExerciseCalibration(exerciseID, gyro_x, gyro_y, gyro_z, rotation_x, rotation_y, rotation_z);

            return true;
        } catch(Exception e){
            //Log.d("save error", e.toString());
            return false;
        }


    }

    //TODO: implement a method where if the two calibrations are too different it warns the user?

    public Hashtable<String, Float> returnGyroVals(){
        Hashtable<String, Float> gyros = new Hashtable<String, Float>();
        /*Hashtable<String, ArrayList<Float>> gyros = new Hashtable<String, ArrayList<Float>>();
        ArrayList<Float> xVals = new ArrayList<Float>();
        ArrayList<Float> yVals = new ArrayList<Float>();
        ArrayList<Float> zVals = new ArrayList<Float>();

        //get the previously stored gyro vals
        if(existingDict != null){
            gyros = existingDict;
            gyroValsX = existingDict.get("gyro_x");
            gyroValsY = existingDict.get("gyro_y");
            gyroValsZ = existingDict.get("gyro_z");
        }*/

        //get the newest gyro vals
        Float gyroZ = new Float(getGyroZ());
        Float gyroX = new Float(getGyroX());
        Float gyroY = new Float(getGyroY());

        //see if the values have changed enough to worry about
        /*if(gyroValsX!=null && gyroValsX.size() >= 1){
            if(changeThreshold(gyroX, gyroValsX.get(gyroValsX.size()-1)) == true
                || changeThreshold(gyroY, gyroValsY.get(gyroValsY.size()-1)) == true
                || changeThreshold(gyroZ, gyroValsZ.get(gyroValsZ.size()-1)) == true
            ){
                gyroValsX.add(gyroX);
                gyroValsY.add(gyroY);
                gyroValsZ.add(gyroZ);
            }
        } else{
            gyroValsX.add(gyroX);
            gyroValsY.add(gyroY);
            gyroValsZ.add(gyroZ);
        }*/

        gyros.put("gyro_x", gyroX);
        gyros.put("gyro_y", gyroY);
        gyros.put("gyro_z", gyroZ);

        return gyros;
    }

    public Hashtable<String, Float> returnRotationVals(){
        Hashtable<String, Float> rotations = new Hashtable<String, Float>();

        //get the rotation vector vals
        Float rotationX = new Float(getRotationX());
        Float rotationY = new Float(getRotationY());
        Float rotationZ = new Float(getRotationZ());

        rotations.put("rotation_x", rotationX);
        rotations.put("rotation_y", rotationY);
        rotations.put("rotation_z", rotationZ);

        return rotations;
    }

    // determine how much time has passed during exercise
    private double timer(){
        //TODO: remove method
        return 0.0;
    }

    private Hashtable <String, Float> getMOE(String eventType){
        //TODO: get these from storage unless it's always the same
        //we need to add an argument that's the exrcise id to do that
        Hashtable<String, Float> marginsOfError = new Hashtable<String, Float>();
        if(eventType == "GYROSCOPE"){
            marginsOfError.put("moe_x", 0.1F);
            marginsOfError.put("moe_y", 0.1F);
            marginsOfError.put("moe_z", 0.1F);
        } else if(eventType=="ROTATION_VECTOR"){
            marginsOfError.put("moe_x", 0.01F);
            marginsOfError.put("moe_y", 0.01F);
            marginsOfError.put("moe_z", 0.01F);
        }
        return marginsOfError;
    }

    public boolean exerciseTracker(String eventType){
        Float x;
        Float y;
        Float z;
        if(eventType=="GYROSCOPE"){
            x = returnGyroVals().get("gyro_x");
            y = returnGyroVals().get("gyro_y");
            z = returnGyroVals().get("gyro_z");
        }
        else if(eventType=="ROTATION_VECTOR"){
            x = returnRotationVals().get("rotation_x");
            y = returnRotationVals().get("rotation_y");
            z = returnRotationVals().get("rotation_z");
        } else{
            return false; //TODO: may want better error catching here
        }

        //TODO: it is probably worth testing and comparing to the 3d distance between points, at least for rotation
        //TODO: probably worth implementing a speed check vs margin of error here (could compare to hard coded value or mean +/- from calibration
        if(eventType=="ROTATION_VECTOR"){
            if(x > calibrationRotationX + marginsOfErrorRotation.get("moe_x") ||
                    x < calibrationRotationX - marginsOfErrorRotation.get("moe_x") ||
                    y > calibrationRotationY + marginsOfErrorRotation.get("moe_y") ||
                    y < calibrationRotationY - marginsOfErrorRotation.get("moe_y") ||
                    z > calibrationRotationZ + marginsOfErrorRotation.get("moe_z") ||
                    z < calibrationRotationZ - marginsOfErrorRotation.get("moe_z")
            ){
                return false;
            } else{
                return true;
            }
        } else if(eventType=="GYROSCOPE"){
            if(x > calibrationGyroX + marginsOfErrorGyro.get("moe_x") ||
                    x < calibrationGyroX - marginsOfErrorGyro.get("moe_x") ||
                    y > calibrationGyroY + marginsOfErrorGyro.get("moe_y") ||
                    y < calibrationGyroY - marginsOfErrorGyro.get("moe_y") ||
                    z > calibrationGyroZ + marginsOfErrorGyro.get("moe_z") ||
                    z < calibrationGyroZ - marginsOfErrorGyro.get("moe_z")
            ){
                return false;
            } else{
                return true;
            }
        } else{
            return true; // in case somethign went wrong somehow we'll just say they're ok
        }

    }


    public void updateEvent(SensorEvent eventIn, String currentEventType) {
        if(currentEventType==null) {
            if(eventType=="GYROSCOPE"){
                this.gyroSensor = eventIn;
            }
            else if(eventType=="ROTATION_VECTOR"){
                this.rotationSensor = eventIn;
            }
        } else{
            if(currentEventType=="GYROSCOPE"){
                this.gyroSensor = eventIn;
            }
            else if(currentEventType=="ROTATION_VECTOR"){
                this.rotationSensor = eventIn;
            }
        }
    }

}