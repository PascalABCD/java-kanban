package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> addedTasks = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private static class Node<Task> {
        public Node<Task> next;
        public Node<Task> prev;
        public Task data;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (addedTasks.containsKey(id)) {
            removeNode(addedTasks.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task e) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, e, null);
        tail = newNode;
        addedTasks.put(e.getId(), newNode);
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentTask = head;

        while (currentTask != null) {
            tasks.add(currentTask.data);
            currentTask = currentTask.next;
        }

        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }

            addedTasks.remove(node.data.getId());
        }
    }
}
