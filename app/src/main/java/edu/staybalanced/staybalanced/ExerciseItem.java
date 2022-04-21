package edu.staybalanced.staybalanced;

public class ExerciseItem {
    // TODO The data in this class should be able to contain the data from the db
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
