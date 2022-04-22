package edu.staybalanced.staybalanced;

public class IconItem {
    private int img;
    private String name;

    public IconItem(int id, String name) {
        this.img = id;
        this.name = name;
    }

    public int getImgId() {
        return img;
    }

    public String getName() { return name; }
}
