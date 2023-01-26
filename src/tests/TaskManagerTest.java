package tests;

import manager.TaskManager;
import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    @BeforeEach
    void renewManager() {
        clearManager();

        for (Task task : tasks) {
            manager.createTask(task);
        }

        for (Epic epic : epics) {
            manager.createEpic(epic);
        }

        for (Subtask subtask : subtasks) {
            subtask.setEpic(epics.get(0));
            manager.createSubtask(subtask);
        }
    }

    void clearManager() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    List<Task> tasks = List.of(
            new Task(1, "Задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.MAX, 0),
            new Task(2, "Задача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.MAX, 0));

    List<Subtask> subtasks = List.of(
            new Subtask(5, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, null, LocalDateTime.MAX, 0),
            new Subtask(6, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, null, LocalDateTime.MAX, 0),
            new Subtask(7, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, null, LocalDateTime.MAX, 0)
    );

    List<Epic> epics = List.of(
            new Epic(3, "Эпик 1", "Описание 1", TaskStatus.NEW),
            new Epic(4, "Эпик 2", "Описание 2", TaskStatus.NEW)
    );


    @Test
    void getAllTasks() {
        Assertions.assertEquals(tasks, manager.getAllTasks());
        clearManager();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllTasks());
    }

    @Test
    void getAllEpics() {
        Assertions.assertEquals(epics.size(), manager.getAllEpics().size());
        clearManager();
        Assertions.assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void getAllSubtasks() {
        Assertions.assertEquals(subtasks, manager.getAllSubtasks());
        clearManager();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllEpics());
    }

    @Test
    void getTaskById() {
        Task task = tasks.get(0);
        Assertions.assertEquals(task, manager.getTaskById(task.getId()));
        Assertions.assertEquals(null, manager.getTaskById(-1));
        manager.deleteAllTasks();
        Assertions.assertEquals(null, manager.getTaskById(task.getId()));
    }

    @Test
    void getEpicById() {
        Epic epic = epics.get(0);
        Assertions.assertEquals(epic.getName(), manager.getEpicById((epic.getId())).getName());
        Assertions.assertEquals(null, manager.getEpicById(-1));
        manager.deleteAllEpics();
        Assertions.assertEquals(null, manager.getEpicById(epic.getId()));
    }

    @Test
    void getSubtaskById() {
        Subtask subtask = manager.getSubtaskById(5);
        Assertions.assertEquals("Подзадача 1", subtask.getName());
        Assertions.assertEquals(null, manager.getSubtaskById(-1));
        manager.deleteAllSubtasks();
        Assertions.assertEquals(null, manager.getSubtaskById(5));
    }

    @Test
    void createTask() {
        Task newTask = new Task(8, "Задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.MAX, 0);
        manager.createTask(newTask);
        Assertions.assertEquals(tasks.size() + 1, manager.getAllTasks().size());
        Assertions.assertEquals(newTask, manager.getAllTasks().get(tasks.size()));
    }

    @Test
    void createEpic() {
        Epic newEpic = new Epic(8, "Эпик 1", "Описание 1", TaskStatus.NEW);
        manager.createEpic(newEpic);
        Assertions.assertEquals(epics.size() + 1, manager.getAllEpics().size());
        Assertions.assertEquals(newEpic, manager.getAllEpics().get(epics.size()));
    }

    @Test
    void createSubtask() {
        Subtask newSubtask = new Subtask(8, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epics.get(0), LocalDateTime.MAX, 0);
        manager.createSubtask(newSubtask);
        Assertions.assertEquals(subtasks.size() + 1, manager.getAllSubtasks().size());
        Assertions.assertEquals(newSubtask, manager.getAllSubtasks().get(subtasks.size()));
    }

    @Test
    void updateTask() {
        Task newTask = new Task(1, "NewName", "NewDescription", TaskStatus.DONE, LocalDateTime.MAX, 0);
        manager.updateTask(newTask);
        Assertions.assertEquals(newTask, manager.getTaskById(newTask.getId()));
        newTask.setId(-1);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> manager.updateTask(newTask));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void updateEpic() {
        Epic newEpic = new Epic(3, "NewName", "NewDescription", TaskStatus.DONE);
        manager.updateEpic(newEpic);
        Assertions.assertEquals(newEpic.getName(), manager.getEpicById(newEpic.getId()).getName());
        Assertions.assertEquals(newEpic.getDescription(), manager.getEpicById(newEpic.getId()).getDescription());

        newEpic.setId(-1);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> manager.updateEpic(newEpic));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void updateSubtask() {
        Subtask newSubtask = new Subtask(5, "NewName", "NewDescription", TaskStatus.DONE, epics.get(0));
        manager.updateSubtask(newSubtask);

        Assertions.assertEquals(newSubtask.getName(), manager.getSubtaskById(5).getName());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(3).getStatus());

        newSubtask.setId(-1);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> manager.updateSubtask(newSubtask));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));

        newSubtask.setId(5);
        newSubtask.setEpic(null);
        ex = Assertions.assertThrows(RuntimeException.class, () -> manager.updateSubtask(newSubtask));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void deleteAllTasks() {
        manager.deleteAllTasks();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllTasks());
        manager.deleteAllTasks();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllTasks());
    }

    @Test
    void deleteAllEpics() {
        manager.deleteAllEpics();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllEpics());
        manager.deleteAllEpics();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllEpics());
    }

    @Test
    void deleteAllSubtasks() {
        manager.deleteAllSubtasks();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllSubtasks());
        manager.deleteAllSubtasks();
        Assertions.assertEquals(Collections.emptyList(), manager.getAllSubtasks());
    }

    @Test
    void deleteTaskById() {
        manager.deleteTaskById(1);
        List expected = List.of(tasks.get(1));
        Assertions.assertEquals(expected, manager.getAllTasks());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> manager.deleteTaskById(-1));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void deleteEpicById() {
        manager.deleteEpicById(3);
        List expected = List.of(epics.get(1));
        Assertions.assertEquals(expected, manager.getAllEpics());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> manager.deleteEpicById(-1));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void deleteSubtaskById() {
        manager.deleteSubtaskById(5);
        List expected = List.of(subtasks.get(1), subtasks.get(2));
        Assertions.assertEquals(expected, manager.getAllSubtasks());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> manager.deleteSubtaskById(-1));
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void getHistory() {
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);

        List<Integer> result = manager.getHistory().stream().map(x -> x.getId()).collect(Collectors.toList());

        Assertions.assertTrue(result.equals(List.of(7, 6, 5, 4, 3, 2, 1)));

        //Test #3
        manager.getEpicById(4);
        manager.getTaskById(1);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getSubtaskById(7);

        result = manager.getHistory().stream().map(x -> x.getId()).collect(Collectors.toList());

        Assertions.assertTrue(result.equals(List.of(7, 3, 2, 6, 5, 1, 4)));


        //Test #4
        manager.getTaskById(1);

        result = manager.getHistory().stream().map(x -> x.getId()).collect(Collectors.toList());

        Assertions.assertTrue((result.equals(List.of(1, 7, 3, 2, 6, 5, 4))));


        //Test #5
        manager.deleteTaskById(2);

        result = manager.getHistory().stream().map(x -> x.getId()).collect(Collectors.toList());

        Assertions.assertTrue(result.equals(List.of(1, 7, 3, 6, 5, 4)));


        //Test #5
        manager.deleteEpicById(3);

        result = manager.getHistory().stream().map(x -> x.getId()).collect(Collectors.toList());

        Assertions.assertTrue((result.equals(List.of(1, 4))));
    }

    @Test
    void getSubtasksByEpicId() {
        Assertions.assertEquals(
                subtasks,
                new ArrayList(manager.getSubtasksByEpicId(epics.get(0).getId()).values())
        );

        Assertions.assertEquals(
                Collections.emptyList(),
                new ArrayList<>(manager.getSubtasksByEpicId(epics.get(1).getId()).values())
        );

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> manager.getSubtasksByEpicId(-1)
        );
        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }
}