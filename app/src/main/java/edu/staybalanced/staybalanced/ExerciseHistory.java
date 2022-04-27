package edu.staybalanced.staybalanced;

import androidx.annotation.NonNull;

// not all default setters/getters are used, suppress warning for it
@SuppressWarnings("unused")
public class ExerciseHistory {

    // ExerciseHistory { historyId: Numerical , exercisesId: Numerical,  epochSeconds: Numerical, secondsInPosition: Numerical }
    private int id;
    private int exerciseId;
    private long epochSeconds;
    private int secondsInPosition;

    // Default constructors
    public ExerciseHistory(int id, int exerciseId, long epochSeconds, int secondsInPosition) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.epochSeconds = epochSeconds;
        this.secondsInPosition = secondsInPosition;
    }
    public ExerciseHistory(){}

    @NonNull
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
    public long getEpochSeconds() {
        return epochSeconds;
    }
    public void setEpochSeconds(long epochSeconds) {
        this.epochSeconds = epochSeconds;
    }
    public int getSecondsInPosition() {
        return secondsInPosition;
    }
    public void setSecondsInPosition(int secondsInPosition) {
        this.secondsInPosition = secondsInPosition;
    }
}
