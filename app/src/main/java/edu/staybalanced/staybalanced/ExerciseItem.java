package edu.staybalanced.staybalanced;

public class ExerciseItem {
    // Do not delete current attributes
    String name;
    // image number refers to memory
    int image;
    int id;
    int secondsToExercise;

    public ExerciseItem(int idIn, String nameIn, int imageIn){
        this.id = idIn;
        this.name = nameIn;
        this.image = imageIn;
        this.secondsToExercise = 30;
    }

    public ExerciseItem(int idIn, String nameIn, int imageIn, int secondsToExerciseIn) {
        this.id = idIn;
        this.name = nameIn;
        this.image = imageIn;
        if (secondsToExerciseIn == 0) {
            secondsToExerciseIn = 30;
        }
        this.secondsToExercise = secondsToExerciseIn;
    }

    // This is the id of the exercise
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // This is the id value corresponding to the image memory location
    public int getImage() {
        return image;
    }

    public int getSecondsToExercise() {return secondsToExercise;}
}
