package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> addedTasks = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        public Node next;
        public Node prev;
        public Task data;

        public Node(Node prev, Task data, Node next) {
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
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, e, null);
        tail = newNode;
        addedTasks.put(e.getId(), newNode);
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentTask = head;

        while (currentTask != null) {
            tasks.add(currentTask.data);
            currentTask = currentTask.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            final Node next = node.next;
            final Node prev = node.prev;

            if (prev == null) {
                head = next;
                if (head != null) {
                    head.prev = null;
                }
            }

            if (next == null) {
                tail = prev;
                if (tail != null) {
                    tail.next = null;
                }
            }

            addedTasks.remove(node.data.getId());
        }
    }
}
