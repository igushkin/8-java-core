package tests;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;


class HistoryManagerTest {

    private HistoryManager manager = new InMemoryHistoryManager();
    private List<Task> tasks;

    @BeforeEach
    void renewManager() {
        manager = new InMemoryHistoryManager();

        Task task = new Task(1, "TaskName", "TaskDescription", TaskStatus.NEW);
        Epic epic = new Epic(2, "EpicName", "EpicDescription", TaskStatus.NEW);
        Subtask subtask1 = new Subtask(3, "SubtaskName", "SubtaskDescription", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(4, "SubtaskName", "SubtaskDescription", TaskStatus.NEW);

        tasks = new ArrayList<>(List.of(task, epic, subtask1, subtask2));

        for(Task t : tasks){
            this.manager.add(t);
        }
    }

    void fillManagerWithTasks() {
        for (Task task : tasks) {
            manager.add(task);
        }
    }

    @Test
    void emptyHistory() {
        Assertions.assertTrue(manager.getHistory().size() == 0);
        Assertions.assertTrue(HistoryManager.serialize(manager).equals(""));
        Assertions.assertTrue(HistoryManager.deserialize("").equals(Collections.emptyList()));
    }

    @Test
    void add() {
        fillManagerWithTasks();
        Collections.reverse(tasks);
        Assertions.assertEquals(tasks, manager.getHistory());
    }

    @Test
    void addCopies() {
        fillManagerWithTasks();
        fillManagerWithTasks();
        Collections.reverse(tasks);
        Assertions.assertEquals(tasks, manager.getHistory());
    }

    @Test
    void remove() {
        fillManagerWithTasks();
        manager.remove(3);
        tasks.remove(2);
        Collections.reverse(tasks);
        Assertions.assertEquals(tasks, manager.getHistory());
    }

    @Test
    void removeFromBeginning() {
        fillManagerWithTasks();
        manager.remove(4);
        tasks.remove(3);
        Collections.reverse(tasks);
        Assertions.assertEquals(tasks, manager.getHistory());
    }

    @Test
    void removeFromEnd() {
        fillManagerWithTasks();
        manager.remove(1);
        tasks.remove(0);
        Collections.reverse(tasks);
        Assertions.assertEquals(tasks, manager.getHistory());
    }

    @Test
    void getHistory() {
        Assertions.assertEquals(Collections.emptyList(), manager.getHistory());
        fillManagerWithTasks();
        Collections.reverse(tasks);
        Assertions.assertEquals(tasks, manager.getHistory());
    }

    @Test
    void serialize() {
        Assertions.assertEquals("", HistoryManager.serialize(manager));
        fillManagerWithTasks();
        Assertions.assertEquals("1,2,3,4", HistoryManager.serialize(manager));
    }

    @Test
    void deserialize() {
        Assertions.assertEquals(Collections.emptyList(), HistoryManager.deserialize(""));
        fillManagerWithTasks();
        Assertions.assertEquals(List.of(4, 3, 2, 1), HistoryManager.deserialize("4,3,2,1"));
    }
}