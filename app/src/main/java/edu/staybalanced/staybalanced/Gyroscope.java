package edu.staybalanced.staybalanced;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

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

    private static ArrayList<Float> gyroValsX = new ArrayList<Float>();
    private static ArrayList<Float> gyroValsY = new ArrayList<Float>();
    private static ArrayList<Float> gyroValsZ = new ArrayList<Float>();

    private static ArrayList<Float> rotationValsX = new ArrayList<Float>();
    private static ArrayList<Float> rotationValsY = new ArrayList<Float>();
    private static ArrayList<Float> rotationValsZ = new ArrayList<Float>();

    Hashtable<String, Float> calibratedGyroVals = new Hashtable<String, Float>();
    Hashtable<String, Float> calibratedRotationVals = new Hashtable<String, Float>();

    //private static SensorEvent sensorEvent;
    private static SensorEvent gyroSensor;
    private static SensorEvent rotationSensor;

    private static ArrayList<Float> inflectionPoints; // this is where we'll store the points after loading in the proper calibration

    String eventTypeGyro;

    String exerciseName;

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
        if(abs(val1 - val2) > 0.05){
            return true;
        }
        else{
            return false;
        }
    }

    //calculate the 3d distance between points
    private static Float distance3D(Float x1, Float y1, Float z1, Float x2, Float y2, Float z2){
        return (float) sqrt(Math.pow((z2-z1),2) + Math.pow((y2-y1),2) + Math.pow((x2-x1),2));
    }

    private static boolean checkRotation(Float xCalibration, Float yCalibration, Float zCalibration, Float xValue, Float yValue, Float zValue, boolean isDistance){
        Float moe = 0.1F; //TODO: get this dynamically vs hard coding it
        if(isDistance){
           Float dist = distance3D(xCalibration, yCalibration, zCalibration, xValue, yValue, zValue);
           if(dist <= moe){
               return true;
           } else{
               return false;
           }
       } else{
            return false; //TODO: lol there was a reason I added the isDistance variable check but I forgor 💀 why
        }
    }

    // get average of an array list
    private static Float getAvg(ArrayList<Float> vals){
        //TODO: see if there's a more efficient way to find the average
        Float sum = 0.0F;
        int len = vals.size();
        for(int i = 0; i<len; i++){
            sum+=vals.get(i);
        }
        return (float) sum / len;
    }

    // this is really checking the speed of movement
    private static boolean checkGyro(Float gyroX, Float gyroY, Float gyroZ, Float calibrationXAvg, Float calibrationYAvg, Float calibrationZAvg){
        Float moeX = 1.0F;
        Float moeY = 1.0F;
        Float moeZ = 1.0F; //TODO: get these margins of error dynamically

        // returns true only if all values are within reasonable range
        if(gyroX > calibrationXAvg+moeX || gyroX < calibrationXAvg-moeX){
            return false;
        } else if (gyroY > calibrationYAvg+moeY || gyroY < calibrationYAvg-moeY){
            return false;
        } else if (gyroZ > calibrationZAvg+moeZ || gyroZ < calibrationZAvg - moeZ){
            return false;
        } else{
            return true;
        }
    }

    /*private static Hashtable<String, Float> getInflectionPoints(ArrayList<Float> xVals, ArrayList<Float> yVals, ArrayList<Float> zVals){
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
    }*/


    public Hashtable<String, Float> getStoredCalibration(){
        Hashtable<String, Float> calibrations = new Hashtable<String, Float>();
        if(exerciseName == "dummy"){
            calibrations.put("x_val", 1.0F);
            calibrations.put("y_val", 1.0F);
            calibrations.put("z_val", 1.0F);
        }
        return calibrations;
        //TODO: implement with storage
    }

    //returns true if calibration save succeeded
    //false if not
    public boolean saveCalibration(){
        try{
            Float gyroXMOE = 0.1F;
            Float gyroYMOE = 0.1F;
            Float gyroZMOE = 0.1F;

            Float rotationXMOE = 0.1F;
            Float rotationYMOE = 0.1F;
            Float rotationZMOE = 0.1F;

            Float rotation_x = calibratedRotationVals.get("rotation_x");
            Float rotation_y = calibratedRotationVals.get("rotation_y");
            Float rotation_z = calibratedRotationVals.get("rotation_z");

            Float rotation_x_min = rotation_x - rotationXMOE;
            Float rotation_y_min = rotation_y - rotationXMOE;
            Float rotation_z_min = rotation_z - rotationZMOE;
            Float rotation_x_max = rotation_x + rotationXMOE;
            Float rotation_y_max = rotation_y + rotationYMOE;
            Float rotation_z_max = rotation_z + rotationZMOE;

            Float gyro_x = calibratedGyroVals.get("gyro_x");
            Float gyro_y = calibratedGyroVals.get("gyro_y");
            Float gyro_z = calibratedGyroVals.get("gyro_z");

            Float gyro_x_min = gyro_x - gyroXMOE;
            Float gyro_y_min = gyro_y - gyroYMOE;
            Float gyro_z_min = gyro_z - gyroZMOE;
            Float gyro_x_max = gyro_x + gyroXMOE;
            Float gyro_y_max = gyro_y + gyroZMOE;
            Float gyro_z_max = gyro_z + gyroZMOE;

            //STORE
            //TODO: add storage code for gyro and rotation vals here (Jeicy)...
            //TODO ...Store the mins and maxes for all gyro and for all rotation
            //}
            return true;
        } catch(Exception e){
            Log.d("save error", e.toString());
            return false;
        }


    }

    // Checks if the person has started switching back directions
    /*private Hashtable<String, Boolean> didSwitchDirections(){
        Hashtable<String, Boolean> result = new Hashtable<String, Boolean>();
        //TODO: look at the previous and current values to see if inflection point happened
        //TODO: see if using gyro data can help with this (change in sign of value)
        //TODO: see if it's within the range of the point(s) of inflection in calibration
        result.put("directionSwitch", false);
        result.put("withinCalibrationSwitchThreshold", false);
        return result;
    }*/

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
        /*ArrayList<Float> xVals = new ArrayList<Float>();
        ArrayList<Float> yVals = new ArrayList<Float>();
        ArrayList<Float> zVals = new ArrayList<Float>();

        if(existingDict != null){
            rotations = existingDict;
            rotationValsX = existingDict.get("rotation_x");
            rotationValsY = existingDict.get("rotation_y");
            rotationValsZ = existingDict.get("rotation_z");
        }*/

        //get the rotation vector vals
        Float rotationX = new Float(getRotationX());
        Float rotationY = new Float(getRotationY());
        Float rotationZ = new Float(getRotationZ());

        //see if the values have changed enough to worry about
        /*if(rotationValsX!=null && rotationValsX.size() >= 1){
            if(changeThreshold(rotationX, rotationValsX.get(rotationValsX.size()-1)) == true
                    || changeThreshold(rotationY, rotationValsX.get(rotationValsX.size()-1)) == true
                    || changeThreshold(rotationZ, rotationValsX.get(rotationValsX.size()-1)) == true
            ){
                rotationValsX.add(rotationX);
                rotationValsY.add(rotationY);
                rotationValsY.add(rotationZ);

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
        }*/

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

    private Hashtable <String, Float> getMOE(){
        //TODO: this may be better done dynamically based on ranges of values from calibration...
        //TODO ...otherwise we probably want all MOE the same and can remove this function
        Hashtable<String, Float> marginsOfError = new Hashtable<String, Float>();
        if(exerciseName == "dummy"){
            marginsOfError.put("moe_x", 0.1F);
            marginsOfError.put("moe_y", 0.1F);
            marginsOfError.put("moe_z", 0.1F);
        } //TODO: implement real exercises, add separate MOE for gyro and rotation
        return marginsOfError;
    }

    public boolean exerciseTracker(){
        Float x;
        Float y;
        Float z;
        if(eventTypeGyro=="GYROSCOPE"){
            x = returnGyroVals().get("gyro_x");
            y = returnGyroVals().get("gyro_y");
            z = returnGyroVals().get("gyro_z");
        }
        else if(eventTypeGyro=="ROTATION_VECTOR"){
            x = returnRotationVals().get("rotation_x");
            y = returnRotationVals().get("rotation_y");
            z = returnRotationVals().get("rotation_z");
        } else{
            return false; //TODO: may want better error catching here
        }

        Hashtable<String, Float> marginsOfError = new Hashtable<String, Float>();
        marginsOfError = getMOE();

        Hashtable<String, Float> calibrationVals = new Hashtable<String, Float>();
        calibrationVals = getStoredCalibration();

        //TODO: it is probably worth testing and comparing to the 3d distance between points, at least for rotation
        //TODO: probably worth implementing a speed check vs margin of error here (could compare to hard coded value or mean +/- from calibration
        if(x > calibrationVals.get("calibration_x") + marginsOfError.get("moe_x") ||
            x < calibrationVals.get("calibration_x") - marginsOfError.get("moe_x") ||
            y > calibrationVals.get("calibration_y") + marginsOfError.get("moe_y") ||
            y < calibrationVals.get("calibration_y") - marginsOfError.get("moe_y") ||
            z > calibrationVals.get("calibration_z") + marginsOfError.get("moe_z") ||
            z < calibrationVals.get("calibration_z") - marginsOfError.get("moe_z")
        ){
            return false;
        } else{
            return true;
        }

        /*Float moeRotationX = 0.1F;
        Float moeRotationY = 0.1F;
        Float moeRotationZ = 0.1F;
        Float moeGyroX = 0.1F;
        Float moeGyroY= 0.1F;
        Float moeGyroZ = 0.1F;

        ArrayList<Float> calibration_gyro_x = calibrationDict.get("calibration_gyro_x");
        ArrayList<Float>  calibration_gyro_y = calibrationDict.get("calibration_gyro_y");
        ArrayList<Float> calibration_gyro_z = calibrationDict.get("calibration_gyro_z");

        ArrayList<Float> calibration_rotation_x = calibrationDict.get("calibration_rotation_x");
        ArrayList<Float> calibration_rotation_y = calibrationDict.get("calibration_gyro_y");
        ArrayList<Float> calibration_rotation_z = calibrationDict.get("calibration_gyro_z");

        ArrayList<Float> gyro_x = rotationDict.get("gyro_x");
        ArrayList<Float> gyro_y = rotationDict.get("gyro_y");
        ArrayList<Float> gyro_z = rotationDict.get("gyro_z");

        ArrayList<Float> rotation_x = rotationDict.get("rotation_x");
        ArrayList<Float> rotation_y = rotationDict.get("rotation_y");
        ArrayList<Float> rotation_z = rotationDict.get("rotation_z");*/

        /*int xIndex = currX.size() - 1;
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
        }*/
    }

    /*private Hashtable<String, ArrayList<Float>> dummyCalibration(){
        Hashtable<String, ArrayList<Float>> calibrationDict = new Hashtable<String, ArrayList<Float>>();
        ArrayList<Float> dummyXListGyro = new ArrayList<Float>();
        ArrayList<Float> dummyYListGyro = new ArrayList<Float>();
        ArrayList<Float> dummyZListGyro = new ArrayList<Float>();

        ArrayList<Float> dummyXListRotation = new ArrayList<Float>();
        ArrayList<Float> dummyYListRotation = new ArrayList<Float>();
        ArrayList<Float> dummyZListRotation = new ArrayList<Float>();

        calibrationDict.put("calibration_gyro_x", dummyXListGyro);
        calibrationDict.put("calibration_gyro_y", dummyYListGyro);
        calibrationDict.put("calibration_gyro_z", dummyZListGyro);

        calibrationDict.put("calibration_rotation_x", dummyXListRotation);
        calibrationDict.put("calibration_rotation_y", dummyYListRotation);
        calibrationDict.put("calibration_rotation_z", dummyZListRotation);

        return calibrationDict;
    }*/

    //call this from the main ui view to update everything with the newest sensor data
    //it returns everything in a dictionary that's needed to check movement
    //if
    /*public boolean update(SensorEvent event, String eventType, Float calibrationGyroXAvg, Float calibrationGyroYAvg, Float calibrationGyroZAvg){
        //TODO: implement inflection points
        boolean speedOk;
        Float x;
        Float y;
        Float z;

        if(eventType=="GYROSCOPE"){
            this.gyroSensor = event;
            x = getGyroX();
            y = getGyroY();
            z = getGyroZ();
            //compares to
            speedOk = checkGyro(x, y, z, calibrationGyroXAvg, calibrationGyroYAvg, calibrationGyroZAvg);
        }
        else if(eventType=="ROTATION_VECTOR"){
            this.rotationSensor = event;
            speedOk = true;
            //compare to
        } else{
            //this should never happen but just in case
            this.rotationSensor = event;
            speedOk = true;

        }
        return speedOk;
        //TODO: update return type to provide:
        //1) if speed is ok
        //2) if we think they started a new rep
        //3) if we are returning a gyroscope or rotation based result
        //4) if their position/form is too far off
        //5) the most recent array list(s) or hashmaps with newest data readings
    }*/

    // constructor for checking exercise after calibration
    public Gyroscope(SensorEvent sensorEvent, String type, String exerciseName){
        /*
        Get the type of exercise they're doing
        process accordingly
        may want to build methods for each exercise in this class*/
        //this.inflectionPoints = getInflectionPoints(); //TODO: implement
        if(type=="GYROSCOPE"){
            this.gyroSensor = sensorEvent;
        }
        else if(type=="ROTATION_VECTOR"){
            this.rotationSensor = sensorEvent;
        }

    }

    public void updateEvent(SensorEvent eventIn) {
        if(eventTypeGyro=="GYROSCOPE"){
            this.gyroSensor = eventIn;
        }
        else if(eventTypeGyro=="ROTATION_VECTOR"){
            this.rotationSensor = eventIn;
        }
    }

    // constructor for calibration
    public Gyroscope(String eventType){
        this.eventTypeGyro = eventType;

//        if(eventType=="GYROSCOPE"){
//            this.gyroSensor = event;
//        }
//        else if(eventType=="ROTATION_VECTOR"){
//            this.rotationSensor = event;
//
//        }

        //this.gyroVals = new ArrayList<Float>();
    }

    //constructor for *saving* the calibrated values
    //needed b/c we call inside oncreate method
    public Gyroscope(Hashtable<String, Float> calibratedGyroVals, Hashtable<String, Float> calibratedRotationVals){
        this.calibratedGyroVals = calibratedGyroVals;
        this.calibratedRotationVals = calibratedRotationVals;
    }

    //constructor for exercise
    public Gyroscope(String eventType, String exerciseName){
        this.eventTypeGyro = eventType;
        this.exerciseName = exerciseName;

//        if(eventType=="GYROSCOPE"){
//            this.gyroSensor = event;
//        }
//        else if(eventType=="ROTATION_VECTOR"){
//            this.rotationSensor = event;
//
//        }

        //this.gyroVals = new ArrayList<Float>();
    }

}
