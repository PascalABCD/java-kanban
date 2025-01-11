package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasksList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
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
                '}';
    }
}
