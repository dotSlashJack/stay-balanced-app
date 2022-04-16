package edu.staybalanced.staybalanced;

public class ExcerciseItem {
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
