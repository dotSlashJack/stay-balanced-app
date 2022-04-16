package edu.staybalanced.staybalanced;

public class ExerciseHistory {

    // ExerciseHistory { historyId: Numerical , exercisesId: Numerical,  epochSeconds: Numerical, secondsInPosition: Numerical }
    private int id;
    private int exerciseId;
    private int epochSeconds;
    private int secondsInPosition;

    // Default constructors
    public ExerciseHistory(int id, int exerciseId, int epochSeconds, int secondsInPosition) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.epochSeconds = epochSeconds;
        this.secondsInPosition = secondsInPosition;
    }
    public ExerciseHistory(){}

    @Override
    public String toString() {
        return "ExerciseHistory{" +
                "id=" + id +
                ", exerciseId=" + exerciseId +
                ", epochSeconds=" + epochSeconds +
                ", secondsInPosition=" + secondsInPosition +
                '}';
    }

    // Setters and getters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getExerciseId() {
        return exerciseId;
    }
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
    public int getEpochSeconds() {
        return epochSeconds;
    }
    public void setEpochSeconds(int epochSeconds) {
        this.epochSeconds = epochSeconds;
    }
    public int getSecondsInPosition() {
        return secondsInPosition;
    }
    public void setSecondsInPosition(int secondsInPosition) {
        this.secondsInPosition = secondsInPosition;
    }
}
