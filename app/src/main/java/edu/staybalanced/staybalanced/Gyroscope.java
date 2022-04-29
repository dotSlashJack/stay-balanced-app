package edu.staybalanced.staybalanced;

import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;
import java.util.Hashtable;

public class Gyroscope{
    //protected Context context;

    // where we store the calibration values for each sensor until saving them
    Hashtable<String, Float> calibratedGyroVals = new Hashtable<String, Float>();
    Hashtable<String, Float> calibratedRotationVals = new Hashtable<String, Float>();

    // the actual sensor event to get value(s) from
    private static SensorEvent gyroSensor;
    private static SensorEvent rotationSensor;

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

    // constructor for calibration
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

        if (isNull((float) current_exercise.getRotationX())) {
            calibrationRotationX = 0f;
        } else {
            calibrationRotationX = (float) current_exercise.getRotationX();
        }
        //Log.i("rotationXVals", calibrationRotationX.toString());
        if (isNull((float) current_exercise.getRotationY())) {
            calibrationRotationY = 0f;
        } else {
            calibrationRotationY = (float) current_exercise.getRotationY();
        }
        //Log.i("rotationYVals", calibrationRotationY.toString());
        if (isNull((float) current_exercise.getRotationZ())) {
            calibrationRotationZ = 0f;
        } else {
            calibrationRotationZ = (float) current_exercise.getRotationZ();
        }
        //Log.i("rotationZVals", calibrationRotationZ.toString());

        //Log.i("rotationVals", calibratedRotationVals.toString());

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
    /*private static boolean changeThreshold(Float val1, Float val2){
        if(abs(val1 - val2) > 0.01){ //TODO: check this value after several tests
            return true;
        }
        else{
            return false;
        }
    }*/

    //returns true if calibration save succeeded
    //false if not
    public boolean saveCalibration(Context context){
        try{

            Float rotation_x = calibratedRotationVals.get("rotation_x");
            Float rotation_y = calibratedRotationVals.get("rotation_y");
            Float rotation_z = calibratedRotationVals.get("rotation_z");

            Float gyro_x = calibratedGyroVals.get("gyro_x");
            Float gyro_y = calibratedGyroVals.get("gyro_y");
            Float gyro_z = calibratedGyroVals.get("gyro_z");

            DatabaseHelper calibrationSaver = new DatabaseHelper(context);
            calibrationSaver.setExerciseCalibration(exerciseID, gyro_x, gyro_y, gyro_z, rotation_x, rotation_y, rotation_z);

            return true;
        } catch(Exception e){
            //Log.d("save error", e.toString());
            return false;
        }
    }

    public Hashtable<String, Float> returnGyroVals(){
        Hashtable<String, Float> gyros = new Hashtable<String, Float>();

        //get the newest gyro vals
        Float gyroZ = new Float(getGyroZ());
        Float gyroX = new Float(getGyroX());
        Float gyroY = new Float(getGyroY());

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

    private Hashtable <String, Float> getMOE(String eventType){
        Hashtable<String, Float> marginsOfError = new Hashtable<String, Float>();
        if(eventType == "GYROSCOPE"){
            marginsOfError.put("moe_x", 0.3F);
            marginsOfError.put("moe_y", 0.3F);
            marginsOfError.put("moe_z", 0.3F);
        } else if(eventType=="ROTATION_VECTOR"){
            marginsOfError.put("moe_x", 0.3F);
            marginsOfError.put("moe_y", 0.3F);
            marginsOfError.put("moe_z", 0.3F);
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