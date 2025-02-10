package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static model.TaskTypes.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        String header = "id,type,name,status,description,epic\n";
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(header);
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        int maxId = 0;
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> allLines = Files.readAllLines(file.toPath());
            if (!allLines.isEmpty()) {
                allLines.removeFirst();
            }

            for (String line : allLines) {
                Task task = fromString(line);

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                // добавляем сабтаски в материнские эпики
                if (task instanceof Subtask subtask) {
                    Epic epic = manager.epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtask(subtask);
                    }
                }
            }
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
            sb.append(epic.getDescription());
        } else if (task instanceof Subtask subtask) {
            sb.append(SUBTASK).append(",");
            sb.append(subtask.getName()).append(",");
            sb.append(subtask.getStatus()).append(",");
            sb.append(subtask.getDescription()).append(",");
            sb.append(subtask.getEpicId());
        } else {
            sb.append(TASK).append(",");
            sb.append(task.getName()).append(",");
            sb.append(task.getStatus()).append(",");
            sb.append(task.getDescription());
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

        if (type == TASK) {
            return new Task(id, name, description, status);
        } else if (type == SUBTASK) {
            int epicId = parts.length == 6 ? Integer.parseInt(parts[5]) : 0;
            return new Subtask(epicId, id, name, description, status);
        } else {
            return new Epic(id, name, description, status);
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
    public void removeTask(Task task) {
        super.removeTask(task);
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
    public void removeSubtask(Subtask subtask) {
        super.removeSubtask(subtask);
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
