package tests;

import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class EpicTest {

    private final TaskManager taskManager = Managers.getDefault();

    EpicTest() throws IOException {
    }

    @BeforeEach
    void removeAll() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
    }

    @Test
    void withoutSubtasksStatusShouldBeNew() {
        Epic epic = new Epic("Test", "Test descr", TaskStatus.DONE);
        epic.getStatus();
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusShouldBeNewIfAllSubtasksNew() {
        Epic epic = new Epic("Test", "Test descr", TaskStatus.DONE);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(epic, "Subtask1", "SubtaskDescr", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic, "Subtask2", "SubtaskDescr", TaskStatus.NEW);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusShouldBeDoneIfAllSubtasksDone() {
        Epic epic = new Epic("Test", "Test descr", TaskStatus.DONE);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(epic, "Subtask1", "SubtaskDescr", TaskStatus.DONE);
        Subtask subtask2 = new Subtask(epic, "Subtask2", "SubtaskDescr", TaskStatus.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        epic = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void statusShouldBeInProgressIfSubtasksDoneAndNew() {
        Epic epic = new Epic("Test", "Test descr", TaskStatus.DONE);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(epic, "Subtask1", "SubtaskDescr", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic, "Subtask2", "SubtaskDescr", TaskStatus.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        epic = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusShouldBeInProgressIfAllSubtasksInProgress() {
        Epic epic = new Epic("Test", "Test descr", TaskStatus.DONE);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(epic, "Subtask1", "SubtaskDescr", TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic, "Subtask2", "SubtaskDescr", TaskStatus.IN_PROGRESS);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }
}
