package manager;

import model.*;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int taskId);

    void createTask(Task task);

    Task updateTask(Task newTask);

    void removeTask(int taskId);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasks();

    Subtask getSubtaskById(int subtaskId);

    void createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask newSubtask);

    void removeSubtask(int subtaskId);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int epicId);

    void createEpic(Epic epic);

    Epic updateEpic(Epic newEpic);

    void removeEpic(int epicId);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
