package edu.staybalanced.staybalanced;

//TODO maybe show seconds

public class ExerciseItem {
    // Do not delete current attributes
    String name;
    // image number refers to memory
    int image;
    int id;

    public ExerciseItem(int idIn, String nameIn, int imageIn){
        this.id = idIn;
        this.name = nameIn;
        this.image = imageIn;
    }

    // This is the id of the excercise
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
}
