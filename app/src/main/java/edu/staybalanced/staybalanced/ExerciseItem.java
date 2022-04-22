package edu.staybalanced.staybalanced;

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
