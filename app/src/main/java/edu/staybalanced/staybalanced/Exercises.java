package edu.staybalanced.staybalanced;

import androidx.annotation.NonNull;
// not all default setters/getters are used, suppress warning for it
@SuppressWarnings("unused")
public class Exercises {

    // Exercises { id: Numerical, name: String, description: String, sets: Numerical, reps: Numerical, secondsPerRep: Numerical, gyroX: Numerical, gyroY: Numerical, gyroZ: Numerical }
    private int id;
    private String name;
    private String description;
    private int sets;
    private int reps;
    private int secondsPerRep;
    private double gyroX;
    private double gyroY;
    private double gyroZ;
    private double rotationX;
    private double rotationY;
    private double rotationZ;
    private int image;

    // Full constructor
    public Exercises(int id, String name, String description, int sets, int reps, int secondsPerRep, double gyroX, double gyroY, double gyroZ, double rotationX, double rotationY, double rotationZ, int image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.secondsPerRep = secondsPerRep;
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.image = image;
    }

    // Default calibration values constructor
    public Exercises(int id, String name, String description, int sets, int reps, int secondsPerRep, int image){
        this(id, name, description, sets, reps, secondsPerRep, 0, 0, 0, 0, 0, 0, image);
    }

    @NonNull
    @Override
    public String toString() {
        return "Exercises{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sets=" + sets +
                ", reps=" + reps +
                ", secondsPerRep=" + secondsPerRep +
                ", gyroX=" + gyroX +
                ", gyroY=" + gyroY +
                ", gyroZ=" + gyroZ +
                ", rotationX=" + rotationX +
                ", rotationY=" + rotationY +
                ", rotationZ=" + rotationZ +
                ", image=" + image +
                '}';
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getSets() {
        return sets;
    }
    public void setSets(int sets) {
        this.sets = sets;
    }
    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
    public int getSecondsPerRep() {
        return secondsPerRep;
    }
    public void setSecondsPerRep(int secondsPerRep) {
        this.secondsPerRep = secondsPerRep;
    }
    public double getGyroX() {
        return gyroX;
    }
    public void setGyroX(double gyroX) {
        this.gyroX = gyroX;
    }
    public double getGyroY() {
        return gyroY;
    }
    public void setGyroY(double gyroY) {
        this.gyroY = gyroY;
    }
    public double getGyroZ() {
        return gyroZ;
    }
    public void setGyroZ(double gyroZ) {
        this.gyroZ = gyroZ;
    }
    public double getRotationX() {
        return rotationX;
    }
    public void setRotationX(double rotationX) {
        this.rotationX = rotationX;
    }
    public double getRotationY() {
        return rotationY;
    }
    public void setRotationY(double rotationY) {
        this.rotationY = rotationY;
    }
    public double getRotationZ() {
        return rotationZ;
    }
    public void setRotationZ(double rotationZ) {
        this.rotationZ = rotationZ;
    }
    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image =  image;
    }

}
