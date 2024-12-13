import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public int id = 1;

    private int generateId() {
        return id++;
    }

    public ArrayList<Task> getAllTasks() {
        System.out.println("List of all tasks:");
        //I want to see in the console the list of all tasks
        for (Task task : tasks.values()) {
            System.out.println(task.toString());
        }
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
        System.out.println("All tasks were removed successfully");
    }

    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        } else {
            System.out.println("Task with this id does not exist");
            return null;
        }
    }

    public void createTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);

        tasks.put(task.getId(), task);
    }

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

    public void removeTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.remove(task.getId());
            System.out.println("Task was removed successfully.");
        } else {
            System.out.println("Cannot remove. Task does not exist.");
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        System.out.println("List of all subtasks:");
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask.toString());
        }
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllSubtasks() {
        System.out.println("All subtasks were removed successfully");
        for (Epic epic : epics.values()) {
            epic.clearSubtasksList();
            updateEpicStatus(epic);
        }
    }

    public Subtask getSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            return subtasks.get(subtaskId);
        } else {
            System.out.println("Subtask with this id does not exist");
            return null;
        }
    }

    public void createSubtask(Subtask subtask) {
        int subtaskId = generateId();
        subtask.setId(subtaskId);

        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Epic does not exist");
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

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

    public void removeSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.remove(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskById(subtask);
            updateEpicStatus(epic);
            System.out.println("Subtask was removed successfully");
        } else {
            System.out.println("Cannot remove. Subtask does not exist.");
        }
    }


    public ArrayList<Epic> getAllEpics() {
        System.out.println("List of all epics:");
        for (Epic epic : epics.values()) {
            System.out.println(epic.toString());
        }
        return new ArrayList<>(epics.values());
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("All epics were removed successfully");
    }

    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId);
        } else {
            System.out.println("Epic with this id does not exist");
            return null;
        }
    }

    public Epic createEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);

        epics.put(epic.getId(), epic);
        return epic;
    }

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

    public void removeEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.remove(epic.getId());
            epic.clearSubtasksList();
            System.out.println("Epic was removed successfully.");
        } else {
            System.out.println("Cannot remove. Epic does not exist");
        }

    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksList = epic.getSubtasksList();
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












