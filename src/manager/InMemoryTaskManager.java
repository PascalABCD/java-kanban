package manager;

import exceptions.InvalidTaskTimeException;
import model.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager hm = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private int id = 1;

    public int getId() {
        return id;
    }

    private int generateId() {
        return id++;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<Task> getAllTasks() {
        tasks.values().forEach(System.out::println);
        return tasks.values().stream().toList();
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(task -> {
            hm.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
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
        if (isTaskTimeOverlapping(task)) {
            throw new InvalidTaskTimeException("Task time is overlapping with another task");
        }
        int taskId = generateId();
        task.setId(taskId);

        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public Task updateTask(Task newTask) {
        if (isTaskTimeOverlapping(newTask)) {
            throw new InvalidTaskTimeException("Task time is overlapping with another task");
        }

        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.remove(tasks.get(taskId));
            tasks.replace(taskId, newTask);
            if (newTask.getStartTime() != null) {
                prioritizedTasks.add(newTask);
            }

            return newTask;
        } else {
            System.out.println("Cannot update. Task does not exist");
            return null;
        }
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            prioritizedTasks.remove(task);
            tasks.remove(taskId);
            hm.remove(taskId);
        } else {
            System.out.println("Cannot remove. Task does not exist.");
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        subtasks.values().forEach(System.out::println);
        return subtasks.values().stream().toList();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            hm.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        epics.values().forEach(epic -> {
            epic.clearSubtasksList();
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        });

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

        if (isTaskTimeOverlapping(subtask)) {
            throw new InvalidTaskTimeException("Subtask time is overlapping with another task");
        }

        int subtaskId = generateId();
        subtask.setId(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicTimeFields(epics.get(subtask.getEpicId()));
        prioritizedTasks.add(subtask);
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        if (isTaskTimeOverlapping(newSubtask)) {
            throw new InvalidTaskTimeException("Subtask time is overlapping with another task");
        }

        int subtaskId = newSubtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask oldSubtask = subtasks.get(subtaskId);
            if (oldSubtask.getEpicId() == newSubtask.getEpicId()) {
                prioritizedTasks.remove(oldSubtask);
                Epic epic = epics.get(newSubtask.getEpicId());
                epic.removeSubtaskById(oldSubtask);
                epic.addSubtask(newSubtask);
                subtasks.replace(subtaskId, newSubtask);
                prioritizedTasks.add(newSubtask);
                updateEpicStatus(epic);
                updateEpicTimeFields(epic);
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
    public void removeSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            Epic epic = epics.get(subtask.getEpicId());
            prioritizedTasks.remove(subtask);
            epic.removeSubtaskById(subtask);
            subtasks.remove(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
            hm.remove(subtask.getId());
        } else {
            System.out.println("Cannot remove. Subtask does not exist.");
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        epics.values().forEach(System.out::println);
        return epics.values().stream().toList();
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubtasksList().forEach(subtask -> {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                hm.remove(subtask.getId());
            });
            hm.remove(epic.getId());
        });

        epics.clear();
        subtasks.clear();
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
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Cannot remove. Epic does not exist");
            return;
        }

        epic.getSubtasksList().forEach(subtask -> {
            subtasks.remove(subtask.getId());
            hm.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });

        epics.remove(epicId);
        hm.remove(epicId);
    }


    @Override
    public List<Task> getHistory() {
        return hm.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksList = epic.getSubtasksList();

        if (subtasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allSubtasksDone = subtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean allSubtasksNew = subtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);

        if (allSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicTimeFields(Epic epic) {
        epic.calculateEpicStartTime();
        epic.calculateEpicEndTime();
        epic.calculateEpicDuration();
    }

    private boolean isTaskTimeOverlapping(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId()) // Это проверка, что нам надо отфильтровать саму задачу
                .anyMatch(t -> {
                    LocalDateTime start1 = task.getStartTime();
                    LocalDateTime end1 = task.getEndTime();
                    LocalDateTime start2 = t.getStartTime();
                    LocalDateTime end2 = t.getEndTime();

                    // если даты null, надо вернуть false, пересечения нет
                    if (start1 == null || end1 == null || start2 == null || end2 == null) {
                        return false;
                    }
                    // пересечение, если конец Р2 после начала второй задачи Р3
                    // если старт Р3 до конца второй задачи Р4
                    return end1.isAfter(start2) && start1.isBefore(end2);
                });
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }
}












