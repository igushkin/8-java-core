package tests;

import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileBackedTasksManagerTest extends TaskManagerTest {
    private final Path path = Paths.get(System.getProperty("user.dir"), "data.csv");

    public FileBackedTasksManagerTest() throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        this.manager = Managers.getFileBackedManager(path);
    }

    @BeforeEach
    private void resetContentOfFile() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("");
        }
    }

    @Test
    public void ifFileIsEmptyTaskListShouldBeEmpty() {
        TaskManager localMamager = Managers.getFileBackedManager(path);
        List<Task> tasks = new ArrayList();

        Stream.of(localMamager.getAllTasks(),
                        localMamager.getAllEpics(),
                        localMamager.getAllSubtasks())
                .forEach(tasks::addAll);

        Assertions.assertTrue(tasks.isEmpty());
    }

    @Test
    public void epicWithoutSubtasks() throws IOException {
        Task task = new Task(1, "TaskName", "TaskDescription", TaskStatus.NEW);
        Epic epic = new Epic(2, "EpicName", "EpicDescription", TaskStatus.NEW);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description");
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getName(), task.getStatus(), task.getDescription(), task.getStartDate(), task.getDuration()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getName(), epic.getStatus(), epic.getDescription(), epic.getStartDate(), epic.getDuration()));
        }

        TaskManager localMamager = FileBackedTasksManager.loadFromFile(path);

        Assertions.assertTrue(localMamager.getAllTasks().size() == 1);
        Assertions.assertTrue(localMamager.getAllEpics().size() == 1);
        Assertions.assertTrue(localMamager.getAllTasks().get(0).equals(task));
        Assertions.assertTrue(localMamager.getAllEpics().get(0).equals(epic));
    }

    @Test
    public void epicWithSubtasks() throws IOException {
        Task task = new Task(1, "TaskName", "TaskDescription", TaskStatus.NEW);
        Epic epic = new Epic(2, "EpicName", "EpicDescription", TaskStatus.NEW);
        Subtask subtask1 = new Subtask(3, "SubtaskName", "SubtaskDescription", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask(4, "SubtaskName", "SubtaskDescription", TaskStatus.NEW, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        String comma = ",";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description");
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getName(), task.getStatus(), task.getDescription(), task.getStartDate(), task.getDuration()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getName(), epic.getStatus(), epic.getDescription(), epic.getStartDate(), epic.getDuration()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s", subtask1.getId(), subtask1.getTaskType(), subtask1.getName(), subtask1.getStatus(), subtask1.getDescription(), subtask1.getStartDate(), subtask1.getDuration(), subtask1.getEpic().getId()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s", subtask2.getId(), subtask2.getTaskType(), subtask2.getName(), subtask2.getStatus(), subtask2.getDescription(), subtask2.getStartDate(), subtask2.getDuration(), subtask2.getEpic().getId()));
        }

        TaskManager localMamager = FileBackedTasksManager.loadFromFile(path);

        Assertions.assertTrue(localMamager.getAllTasks().size() == 1);
        Assertions.assertTrue(localMamager.getAllEpics().size() == 1);
        Assertions.assertTrue(localMamager.getAllSubtasks().size() == 2);
        Assertions.assertTrue(localMamager.getAllTasks().get(0).equals(task));
        Assertions.assertTrue(localMamager.getAllEpics().get(0).equals(epic));
    }

    @Test
    public void epicWithSubtasksAndHistory() throws IOException {
        Task task = new Task(1, "TaskName", "TaskDescription", TaskStatus.NEW);
        Epic epic = new Epic(2, "EpicName", "EpicDescription", TaskStatus.NEW);
        Subtask subtask1 = new Subtask(3, "SubtaskName", "SubtaskDescription", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask(4, "SubtaskName", "SubtaskDescription", TaskStatus.NEW, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description");
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getName(), task.getStatus(), task.getDescription(), task.getStartDate(), task.getDuration()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getName(), epic.getStatus(), epic.getDescription(), task.getStartDate(), task.getDuration()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s", subtask1.getId(), subtask1.getTaskType(), subtask1.getName(), subtask1.getStatus(), subtask1.getDescription(), subtask1.getStartDate(), subtask1.getDuration(), subtask1.getEpic().getId()));
            bw.write(System.lineSeparator());
            bw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s", subtask2.getId(), subtask2.getTaskType(), subtask2.getName(), subtask2.getStatus(), subtask2.getDescription(), subtask2.getStartDate(), subtask2.getDuration(), subtask2.getEpic().getId()));
            bw.write(System.lineSeparator());
            bw.write(System.lineSeparator());
            bw.write("4,3,2,1");
        }

        TaskManager restoredManager = FileBackedTasksManager.loadFromFile(path);

        Assertions.assertTrue(
                restoredManager.getHistory()
                        .stream()
                        .map(x -> x.getId())
                        .collect(Collectors.toList())
                        .equals(List.of(1, 2, 3, 4)));
    }
}