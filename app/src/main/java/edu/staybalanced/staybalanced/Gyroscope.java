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

    //private static SensorEvent sensorEvent;
    private static SensorEvent gyroSensor;
    private static SensorEvent rotationSensor;

    private static ArrayList<Float> inflectionPoints; // this is where we'll store the points after loading in the proper calibration

    String eventTypeGyro;

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


    public Hashtable<String, ArrayList<Float>> getStoredCalibration(){
        return new Hashtable<String, ArrayList<Float>>();
        //TODO: implement with storage
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
        //TODO: want to store range of values, mean of values for 2 calibrations
    }

    // Checks if the person has started switching back directions
    private Hashtable<String, Boolean> didSwitchDirections(){
        Hashtable<String, Boolean> result = new Hashtable<String, Boolean>();
        //TODO: look at the previous and current values to see if inflection point happened
        //TODO: see if using gyro data can help with this (change in sign of value)
        //TODO: see if it's within the range of the point(s) of inflection in calibration
        result.put("directionSwitch", false);
        result.put("withinCalibrationSwitchThreshold", false);
        return result;
    }

    //TODO: implement a method where if the two calibrations are too different it warns the user?

    public Hashtable<String, ArrayList<Float>> returnGyroVals(Hashtable<String, ArrayList<Float>> existingDict){
        Hashtable<String, ArrayList<Float>> gyros = new Hashtable<String, ArrayList<Float>>();
        ArrayList<Float> xVals = new ArrayList<Float>();
        ArrayList<Float> yVals = new ArrayList<Float>();
        ArrayList<Float> zVals = new ArrayList<Float>();

        //get the previously stored gyro vals
        if(existingDict != null){
            gyros = existingDict;
            gyroValsX = existingDict.get("gyro_x");
            gyroValsY = existingDict.get("gyro_y");
            gyroValsZ = existingDict.get("gyro_z");
        }

        //get the newest gyro vals
        Float gyroZ = new Float(getGyroZ());
        Float gyroX = new Float(getGyroX());
        Float gyroY = new Float(getGyroY());

        //see if the values have changed enough to worry about
        if(gyroValsX!=null && gyroValsX.size() >= 1){
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
        }

        gyros.put("gyro_x", gyroValsX);
        gyros.put("gyro_y", gyroValsY);
        gyros.put("gyro_z", gyroValsZ);

        return gyros;
    }

    public Hashtable<String, ArrayList<Float>> returnRotationVals(Hashtable<String, ArrayList<Float>> existingDict){
        Hashtable<String, ArrayList<Float>> rotations = new Hashtable<String, ArrayList<Float>>();
        ArrayList<Float> xVals = new ArrayList<Float>();
        ArrayList<Float> yVals = new ArrayList<Float>();
        ArrayList<Float> zVals = new ArrayList<Float>();

        if(existingDict != null){
            rotations = existingDict;
            rotationValsX = existingDict.get("rotation_x");
            rotationValsY = existingDict.get("rotation_y");
            rotationValsZ = existingDict.get("rotation_z");
        }

        //get the rotation vector vals
        Float rotationX = new Float(getRotationX());
        Float rotationY = new Float(getRotationY());
        Float rotationZ = new Float(getRotationZ());

        //see if the values have changed enough to worry about
        if(rotationValsX!=null && rotationValsX.size() >= 1){
            if(changeThreshold(rotationX, rotationValsX.get(rotationValsX.size()-1)) == true
                    || changeThreshold(rotationY, rotationValsX.get(rotationValsX.size()-1)) == true
                    || changeThreshold(rotationZ, rotationValsX.get(rotationValsX.size()-1)) == true
            ){
                rotationValsX.add(rotationX);
                rotationValsY.add(rotationY);
                rotationValsY.add(rotationZ);

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

        rotations.put("rotation_x", rotationValsX);
        rotations.put("rotation_y", rotationValsY);
        rotations.put("rotation_z", rotationValsZ);

        return rotations;
    }

    // determine how much time has passed during exercise
    private double timer(){
        //TODO: implement this method
        return 0.0;
    }

    private boolean exerciseTracker(Hashtable<String, ArrayList<Float>> calibrationDict, Hashtable<String, ArrayList<Float>> gyroDict, Hashtable<String, ArrayList<Float>> rotationDict){
        Float moeRotationX = 0.1F;
        Float moeRotationY = 0.1F;
        Float moeRotationZ = 0.1F;
        Float moeGyroX = 0.1F;
        Float moeGyroY= 0.1F;
        Float moeGyroZ = 0.1F;
        //TODO: Calculate these margins of error based on a data range instead of hardcoded values

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
        ArrayList<Float> rotation_z = rotationDict.get("rotation_z");

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
        return true;//TODO: update to accurate return
    }

    private Hashtable<String, ArrayList<Float>> dummyCalibration(){
        Hashtable<String, ArrayList<Float>> calibrationDict = new Hashtable<String, ArrayList<Float>>();
        ArrayList<Float> dummyXListGyro = new ArrayList<Float>();
        ArrayList<Float> dummyYListGyro = new ArrayList<Float>();
        ArrayList<Float> dummyZListGyro = new ArrayList<Float>();

        /*for(int i = 0; i<xGyroList.size(); i++){
            dummyXListGyro.add()
        }*/

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
    }

    //call this from the main ui view to update everything with the newest sensor data
    //it returns everything in a dictionary that's needed to check movement
    //if
    public boolean update(SensorEvent event, String eventType, Float calibrationGyroXAvg, Float calibrationGyroYAvg, Float calibrationGyroZAvg){
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
    }

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

    // constructor for initialization/calibration
    //public Gyroscope(SensorEvent gyroEvent, SensorEvent vectorEvent){
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

}
