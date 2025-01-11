package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
            if (history.size() > 10) {
                history.removeFirst();
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("List of all tasks in history:");
        for (Task task : history) {
            System.out.println(task.toString());
        }
        return new ArrayList<>(history);
    }
}
