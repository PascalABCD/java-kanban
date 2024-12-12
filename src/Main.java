public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Task Manager!");

        TaskManager tm = new TaskManager();
        Task task1 = new Task("Task 1", "Description 1", Status.DONE);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        Epic epic1 = new Epic("Epic 1", "Description epic 1");
        Epic epic2 = new Epic("Epic 2", "Description epic 2");

        tm.createTask(task1);
        tm.createTask(task2);

        tm.createEpic(epic1);
        tm.createEpic(epic2);

        Subtask subtask1 = new Subtask(epic1, "Subtask 1", "Description subtask 1", Status.NEW);
        Subtask subtask2 = new Subtask(epic1, "Subtask 2", "Description subtask 3", Status.IN_PROGRESS);
        Subtask subtask3 = new Subtask(epic2,"Subtask 3", "Description subtask 3", Status.NEW);

        tm.createSubtask(subtask1);
        tm.createSubtask(subtask2);
        tm.createSubtask(subtask3);

        tm.getAllTasks();
        tm.getSubtasks();
        tm.getAllEpics();

        System.out.println("Updating task status... updating subtask status... updating epic status...");
        task1.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        tm.updateTask(task1);
        tm.updateSubtask(subtask1);
        tm.updateSubtask(subtask3);
        tm.updateSubtask(subtask3);
        tm.updateEpicStatus(epic1);
        tm.updateEpicStatus(epic2);
        System.out.println("Status update complete.");

        System.out.println("Getting info for tasks, subtasks, and epics...");
        tm.getAllTasks();
        tm.getSubtasks();
        tm.getAllEpics();

        System.out.println("Removing epic 2...");
        tm.removeEpic(epic2);
        tm.getAllEpics();

        System.out.println("Removing task 1...");
        tm.removeTask(task1);
        tm.getAllTasks();






    }
}
