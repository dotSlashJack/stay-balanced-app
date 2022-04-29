package edu.staybalanced.staybalanced;

import androidx.annotation.NonNull;

// not all default setters/getters are used, suppress warning for it
@SuppressWarnings("unused")
public class UnlockedAssets {

    // UnlockedAssets { image: Numerical, description: String, unlocked: Boolean }
    private int image;
    private String description;
    private Boolean unlocked;

    public UnlockedAssets(int image, String description, Boolean unlocked) {
        this.image = image;
        this.description = description;
        this.unlocked = unlocked;
    }
    public UnlockedAssets() {
    }

    @NonNull
    @Override
    public String toString() {
        return "UnlockedAssets{" +
                "id=" + image +
                ", resourceFilename='" + description + '\'' +
                ", unlocked=" + unlocked +
                '}';
    }

    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) { this.description = description; }
    public Boolean getUnlocked() {
        return unlocked;
    }
    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }
}
