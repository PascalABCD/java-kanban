package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static model.TaskTypes.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        String header = "id,type,name,status,description,duration,startTime,epic\n";
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(header);

            Stream.of(getAllTasks(), getAllSubtasks(), getAllEpics())
                    .flatMap(Collection::stream)
                    .map(this::toString)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString + "\n");
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения " + e.getMessage());
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> allLines = Files.readAllLines(file.toPath());
            if (allLines.isEmpty()) {
                return manager;
            }

            int maxId = allLines.stream()
                    .skip(1) // это заголовок, не нужен
                    .map(FileBackedTaskManager::fromString)
                    .peek(task -> {
                        if (task instanceof Epic) {
                            manager.epics.put(task.getId(), (Epic) task);
                        } else if (task instanceof Subtask subtask) {
                            manager.subtasks.put(task.getId(), subtask);
                            Epic epic = manager.epics.get(subtask.getEpicId());
                            if (epic != null) {
                                epic.addSubtask(subtask);
                            }
                        } else {
                            manager.tasks.put(task.getId(), task);
                        }
                    })
                    .mapToInt(Task::getId)
                    .max()
                    .orElse(0);

            manager.setId(maxId + 1);

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки из файла " + e.getMessage());
        }

        return manager;
    }


    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");

        if (task instanceof Epic epic) {
            sb.append(EPIC).append(",");
            sb.append(epic.getName()).append(",");
            sb.append(epic.getStatus()).append(",");
            sb.append(epic.getDescription()).append(",");
            sb.append(task.getDuration()).append(",");
            sb.append(task.getStartTime());
        } else if (task instanceof Subtask subtask) {
            sb.append(SUBTASK).append(",");
            sb.append(subtask.getName()).append(",");
            sb.append(subtask.getStatus()).append(",");
            sb.append(subtask.getDescription()).append(",");
            sb.append(task.getDuration()).append(",");
            sb.append(task.getStartTime()).append(",");
            sb.append(subtask.getEpicId());
        } else {
            sb.append(TASK).append(",");
            sb.append(task.getName()).append(",");
            sb.append(task.getStatus()).append(",");
            sb.append(task.getDescription()).append(",");
            sb.append(task.getDuration()).append(",");
            sb.append(task.getStartTime());
        }
        return sb.toString();
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskTypes type = TaskTypes.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        // parse text to Duration, check not null
        Duration duration = Duration.parse(parts[5]);
        LocalDateTime startTime = !parts[6].isEmpty() ? LocalDateTime.parse(parts[6]) : null;

        if (type == TASK) {
            return new Task(id, name, description, status, duration, startTime);
        } else if (type == SUBTASK) {
            int epicId = parts.length == 8 ? Integer.parseInt(parts[7]) : 0;
            return new Subtask(epicId, id, name, description, status, duration, startTime);
        } else {
            return new Epic(id, name, description, status, duration, startTime);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Task updateTask(Task newTask) {
        Task task = super.updateTask(newTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }

        return task;
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask subtask = super.updateSubtask(newSubtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return subtask;
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic epic = super.updateEpic(newEpic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }

        return epic;
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
}
