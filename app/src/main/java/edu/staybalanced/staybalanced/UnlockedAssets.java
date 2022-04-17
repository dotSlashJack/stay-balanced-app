package edu.staybalanced.staybalanced;

public class UnlockedAssets {

    // UnlockedAssets { assetId: Numerical, resourceFilename: String, unlocked: Boolean }
    private int id;
    private String resourceFilename;
    private Boolean unlocked;

    public UnlockedAssets(int id, String resourceFilename, Boolean unlocked) {
        this.id = id;
        this.resourceFilename = resourceFilename;
        this.unlocked = unlocked;
    }
    public UnlockedAssets() {
    }

    @Override
    public String toString() {
        return "UnlockedAssets{" +
                "id=" + id +
                ", resourceFilename='" + resourceFilename + '\'' +
                ", unlocked=" + unlocked +
                '}';
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getResourceFilename() {
        return resourceFilename;
    }
    public void setResourceFilename(String resourceFilename) {
        this.resourceFilename = resourceFilename;
    }
    public Boolean getUnlocked() {
        return unlocked;
    }
    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }
}
