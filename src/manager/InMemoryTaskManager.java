package manager;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager hm = Managers.getDefaultHistory();

    private int id = 1;

    private int generateId() {
        return id++;
    }

    @Override
    public List<Task> getAllTasks() {
        System.out.println("List of all tasks:");
        for (Task task : tasks.values()) {
            System.out.println(task.toString());
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            hm.remove(task.getId());
        }
        tasks.clear();
        System.out.println("All tasks were removed successfully");
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            hm.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            System.out.println("Task with this id does not exist");
            return null;
        }
    }

    @Override
    public void createTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);

        tasks.put(task.getId(), task);
    }

    @Override
    public Task updateTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            tasks.replace(taskId, newTask);

            return newTask;
        } else {
            System.out.println("Cannot update. Task does not exist");
            return null;
        }
    }

    @Override
    public void removeTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.remove(task.getId());
            hm.remove(task.getId());
            System.out.println("Task was removed successfully.");
        } else {
            System.out.println("Cannot remove. Task does not exist.");
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        System.out.println("List of all subtasks:");
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask.toString());
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubtasks() {
        System.out.println("All subtasks were removed successfully");
        for (Subtask subtask : subtasks.values()) {
            hm.remove(subtask.getId());
        }

        for (Epic epic : epics.values()) {
            epic.clearSubtasksList();
            updateEpicStatus(epic);
        }

        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            hm.add(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        } else {
            System.out.println("Subtask with this id does not exist");
            return null;
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Epic does not exist");
            return;
        }

        int subtaskId = generateId();
        subtask.setId(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        int subtaskId = newSubtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask oldSubtask = subtasks.get(subtaskId);
            if (oldSubtask.getEpicId() == newSubtask.getEpicId()) {
                Epic epic = epics.get(newSubtask.getEpicId());
                epic.removeSubtaskById(oldSubtask);
                epic.addSubtask(newSubtask);
                subtasks.replace(subtaskId, newSubtask);
                updateEpicStatus(epic);
                return newSubtask;
            } else {
                System.out.println("Cannot update. Subtask cannot be moved to another epic");
                return null;
            }
        } else {
            System.out.println("Cannot update. Subtask does not exist");
            return null;
        }
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskById(subtask);
            subtasks.remove(subtask.getId());
            updateEpicStatus(epic);
            hm.remove(subtask.getId());
            System.out.println("Subtask was removed successfully");
        } else {
            System.out.println("Cannot remove. Subtask does not exist.");
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        System.out.println("List of all epics:");
        for (Epic epic : epics.values()) {
            System.out.println(epic.toString());
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasksList()) {
                subtasks.remove(subtask.getId());
                hm.remove(subtask.getId());
            }
            hm.remove(epic.getId());
        }

        epics.clear();
        subtasks.clear();
        System.out.println("All epics were removed successfully");
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            hm.add(epics.get(epicId));
            return epics.get(epicId);
        } else {
            System.out.println("Epic with this id does not exist");
            return null;
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);

        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epics.containsKey(epicId)) {
            Epic newEpic = epics.get(epicId);
            newEpic.setName(newEpic.getName());
            newEpic.setDescription(newEpic.getDescription());
            return newEpic;
        } else {
            System.out.println("Epic does not exist");
            return null;
        }
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for (Subtask subtask : epic.getSubtasksList()) {
                subtasks.remove(subtask.getId());
                hm.remove(subtask.getId());
            }
            epics.remove(epicId);
            hm.remove(epicId);
            System.out.println("Epic was removed successfully.");
        } else {
            System.out.println("Cannot remove. Epic does not exist");
        }
    }

    @Override
    public List<Task> getHistory() {
        return hm.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksList = epic.getSubtasksList();
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;
        for (Subtask subtask : subtasksList) {
            if (subtask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allSubtasksNew = false;
            }
        }
        if (allSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}












