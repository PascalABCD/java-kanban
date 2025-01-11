package manager;

import model.*;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int taskId);

    void createTask(Task task);

    Task updateTask(Task newTask);

    void removeTask(Task task);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasks();

    Subtask getSubtaskById(int subtaskId);

    void createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask newSubtask);

    void removeSubtask(Subtask subtask);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int epicId);

    void createEpic(Epic epic);

    Epic updateEpic(Epic newEpic);

    void removeEpic(int epicId);

    List<Task> getHistory();
}
