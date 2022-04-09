package edu.staybalanced.staybalanced;

public class Excercise {
    String name;
    // image number refers to memory
    int image;

    public Excercise(String nameIn, int imageIn){
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
