package manager;

import task.*;

import java.util.*;

import utility.*;

public class InMemoryHistoryManager implements HistoryManager {

    private HashMap<Integer, Node<Task>> nodeMap;
    private Node<Task> first;
    private Node<Task> last;

    public InMemoryHistoryManager() {
        nodeMap = new HashMap<>();
    }

    public InMemoryHistoryManager(List<Task> tasks) {
        nodeMap = new HashMap<>();
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            this.add(task);
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        Node<Task> newNode = new Node(task);
        nodeMap.put(task.getId(), newNode);
        linkLast(newNode);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.get(id);
        if (node == null) {
            return;
        }
        removeNode(node);
        nodeMap.remove(id);
    }

    private void removeNode(Node<Task> node) {
        Node<Task> previous = node.previous;
        Node<Task> next = node.next;

        if (previous == null) {
            this.first = next;
        } else {
            node.previous = null;
            previous.next = next;
        }

        if (next == null) {
            this.last = previous;
        } else {
            node.next = null;
            next.previous = previous;
        }
    }

    private void linkLast(Node<Task> node) {
        if (first == null) {
            first = node;
            last = node;
        } else {
            node.previous = last;
            last.next = node;
            last = node;
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node<Task> node = last;
        while (node != null) {
            list.add(node.val);
            node = node.previous;
        }
        return list;
    }
}