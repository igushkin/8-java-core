package utility;

public class Node<T> {
    public T val;
    public Node<T> next;
    public Node<T> previous;

    public Node() {
    }

    public Node(T value) {
        this.val = value;
    }

    public Node(T value, Node<T> next, Node<T> previous) {
        this.next = next;
        this.previous = previous;
        this.val = value;
    }
}
