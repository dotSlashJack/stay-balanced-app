package edu.staybalanced.staybalanced;

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

    // Default constructors
    public Exercises(int id, String name, String description, int sets, int reps, int secondsPerRep, double gyroX, double gyroY, double gyroZ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.secondsPerRep = secondsPerRep;
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
    }
    public Exercises() {
    }

    // toString
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
    public void setGyroX(int gyroX) {
        this.gyroX = gyroX;
    }
    public double getGyroY() {
        return gyroY;
    }
    public void setGyroY(int gyroY) {
        this.gyroY = gyroY;
    }
    public double getGyroZ() {
        return gyroZ;
    }
    public void setGyroZ(int gyroZ) {
        this.gyroZ = gyroZ;
    }
}
