package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Subtask> subtasksList = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, Status.NEW, duration, startTime);
    }

    public Epic(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public void calculateEpicDuration() {
        duration = subtasksList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public void calculateEpicEndTime() {
        LocalDateTime maxSubtaskEndTime = subtasksList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(subtask -> subtask))
                .orElse(null);
        endTime = maxSubtaskEndTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void calculateEpicStartTime() {
        LocalDateTime minSubtaskstartTime = subtasksList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.comparing(subtask -> subtask))
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
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
