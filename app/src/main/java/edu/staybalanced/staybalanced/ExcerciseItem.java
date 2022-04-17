package edu.staybalanced.staybalanced;

public class ExcerciseItem {
    // TODO The data in this class should be able to contain the data from the db
    // Do not delete current attributes
    String name;
    // image number refers to memory
    int image;

    public ExcerciseItem(String nameIn, int imageIn){
        this.name = nameIn;
        this.image = imageIn;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
