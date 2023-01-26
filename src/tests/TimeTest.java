package tests;

import manager.Managers;
import manager.TaskManager;
import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

class TimeTest {

    private TaskManager manager = Managers.getDefault();

    private LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);
    private int duration = 35;

    List<Task> tasks = List.of(
            new Task(1, "Задача 1", "Описание 1", TaskStatus.NEW));

    List<Epic> epics = List.of(
            new Epic(4, "Эпик 2", "Описание 2", TaskStatus.NEW)
    );

    List<Subtask> subtasks = List.of(
            new Subtask(5, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epics.get(0)),
            new Subtask(6, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, epics.get(0))
    );

    TimeTest() throws IOException {
    }

    @BeforeEach
    void renewManager() {
        deleteAllTypeOfTasks();
        for (Task task : tasks) {
            manager.createTask(task);
        }
        for (Epic epic : epics) {
            manager.createEpic(epic);
        }
        for (Subtask subtask : subtasks) {
            manager.createSubtask(subtask);
        }
    }

    void deleteAllTypeOfTasks() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @Test
    void checkEndTimeWithZeroDuration() {
        Task task = tasks.get(0);
        task.setStartDate(startDate);
        task.setDuration(0);
        manager.updateTask(task);
        task = manager.getTaskById(task.getId());
        Assertions.assertEquals(startDate, task.getEndTime());
    }

    @Test
    void checkEndTimeWithDuration() {
        Task task = tasks.get(0);
        task.setStartDate(startDate);
        task.setDuration(duration);
        manager.updateTask(task);
        task = manager.getTaskById(task.getId());
        Assertions.assertEquals(startDate.plusMinutes(duration), task.getEndTime());
    }

    @Test
    void checkEpicEndTimeWithoutSubtasks() {
        Epic epic = epics.get(0);
        Assertions.assertEquals(LocalDateTime.MAX, epic.getStartDate());
        Assertions.assertEquals(LocalDateTime.MAX, epic.getEndTime());
    }

    @Test
    void checkEpicEndTimeWithSubtasks() {
        Epic epic = epics.get(0);
        Subtask subtask1 = subtasks.get(0);
        Subtask subtask2 = subtasks.get(1);

        LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);

        subtask1.setStartDate(startDate);
        subtask2.setStartDate(startDate.plusMinutes(30));
        subtask1.setDuration(30);
        subtask2.setDuration(10);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        epic = manager.getEpicById(epic.getId());

        Assertions.assertEquals(startDate, epic.getStartDate());
        Assertions.assertEquals(startDate.plusMinutes(40), epic.getEndTime());
    }

    @Test
    void checkCreatingTasksWithoutTimeIntersactions() {
        Task task = tasks.get(0);
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);
        task.setStartDate(startDate);
        task.setDuration(30);

        Task newTask = new Task(2, "Задача 1", "Описание 1", TaskStatus.NEW);
        newTask.setStartDate(startDate.plusMinutes(30));
        newTask.setDuration(30);
        manager.createTask(newTask);

        Assertions.assertEquals(2, manager.getAllTasks().size());
    }

    @Test
    void checkCreatingTasksWithTimeIntersactions1() {
        Task task = tasks.get(0);
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);
        task.setStartDate(startDate);
        task.setDuration(30);
        manager.updateTask(task);

        Task newTask = new Task(2, "Задача 1", "Описание 1", TaskStatus.NEW);
        newTask.setStartDate(startDate.minusMinutes(1));
        newTask.setDuration(30);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> manager.createTask(newTask));

        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void checkCreatingTasksWithTimeIntersactions2() {
        Task task = tasks.get(0);
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);
        task.setStartDate(startDate);
        task.setDuration(30);
        manager.updateTask(task);

        Task newTask = new Task(2, "Задача 1", "Описание 1", TaskStatus.NEW);
        newTask.setStartDate(startDate.minusMinutes(1));
        newTask.setDuration(60);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> manager.createTask(newTask));

        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void checkCreatingTasksWithTimeIntersactions3() {
        Task task = tasks.get(0);
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);
        task.setStartDate(startDate);
        task.setDuration(30);
        manager.updateTask(task);

        Task newTask = new Task(2, "Задача 1", "Описание 1", TaskStatus.NEW);
        newTask.setStartDate(startDate.plusMinutes(1));
        newTask.setDuration(60);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> manager.createTask(newTask));

        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void checkCreatingTasksWithTimeIntersactions4() {
        Task task = tasks.get(0);
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 13, 14, 30);
        task.setStartDate(startDate);
        task.setDuration(30);
        manager.updateTask(task);

        Task newTask = new Task(2, "Задача 1", "Описание 1", TaskStatus.NEW);
        newTask.setStartDate(startDate);
        newTask.setDuration(30);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> manager.createTask(newTask));

        Assertions.assertTrue(ex.toString().contains("RuntimeException"));
    }

    @Test
    void checkPrioritization() {
        Task task = tasks.get(0);
        Epic epic = epics.get(0);
        Subtask subtask1 = subtasks.get(0);
        Subtask subtask2 = subtasks.get(1);

        Task updatedTask = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus(), startDate, 0);
        manager.updateTask(updatedTask);

        Assertions.assertEquals(updatedTask, manager.getPrioritizedTasks().get(0));


        Subtask updatedSubtask = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(), epic, startDate.minusMinutes(5), 0);
        manager.updateSubtask(updatedSubtask);

        Assertions.assertEquals(List.of(manager.getSubtaskById(subtask1.getId()), task, manager.getSubtaskById(subtask2.getId())), manager.getPrioritizedTasks());
    }
}