package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Epic extends Task {
    protected ArrayList<Subtask> subtasksList = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, Status.NEW, duration, startTime);
    }

    public Epic(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public void calculateEpicDuration() {
        duration = subtasksList.stream()
                .map(Subtask::getDuration)
                .filter(subtaskDuration -> subtaskDuration != null)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public void calculateEpicEndTime() {
        LocalDateTime maxSubtaskEndTime = subtasksList.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(subtask -> subtask.getEndTime()))
                .map(Subtask::getEndTime)
                .orElse(null);
        endTime = maxSubtaskEndTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void calculateEpicStartTime() {
        LocalDateTime minSubtaskstartTime = subtasksList.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(subtask -> subtask.getStartTime()))
                .map(Subtask::getStartTime)
                .orElse(null);
        startTime = minSubtaskstartTime;
    }

    public void removeSubtaskById(Subtask subtask) {
        subtasksList.remove(subtask);
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void clearSubtasksList() {
        subtasksList.clear();
    }

    public void addSubtask(Subtask subtask) {
        subtasksList.add(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasksList=" + subtasksList +
                ", duration" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
