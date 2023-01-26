package tests;

import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class TaskTest {

    @Test
    void setters() {
        Task task = new Task(0, "", "", TaskStatus.NEW, LocalDateTime.MAX, 0);
        Task epic = new Epic(0, "", "", TaskStatus.NEW);
        Task subtask = new Subtask(0, "", "", TaskStatus.NEW);

        List<Task> list = List.of(task, epic, subtask);

        for (int x = 0; x < list.size(); x++) {
            int newId = x;
            Task currentTask = list.get(x);
            String newName = "name" + x;
            String newDescription = "description" + x;
            TaskStatus newStatus = TaskStatus.values()[x];

            currentTask.setId(newId);
            currentTask.setName(newName);
            currentTask.setDescription(newDescription);
            currentTask.setStatus(newStatus);

            Assertions.assertEquals(newId, currentTask.getId());
            Assertions.assertEquals(newName, currentTask.getName());
            Assertions.assertEquals(newDescription, currentTask.getDescription());

            if (currentTask.getTaskType() != TaskType.EPIC) {
                Assertions.assertEquals(newStatus, currentTask.getStatus());
            }
        }
    }

    @Test
    void serialization() {
        int id = 0;
        String name = "Name";
        String description = "Description";
        TaskStatus status = TaskStatus.NEW;

        Task task = new Task(id, name, description, status, LocalDateTime.MAX, 0);
        Epic epic = new Epic(id, name, description, status);
        Subtask subtask = new Subtask(id, name, description, status);

        subtask.setEpic(epic);

        String expected1 = String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskType.TASK, name, status, description, task.getStartDate(), task.getDuration());
        String expected2 = String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskType.EPIC, name, status, description, epic.getStartDate(), epic.getDuration());
        String expected3 = String.format("%s,%s,%s,%s,%s,%s,%s,%s", id, TaskType.SUBTASK, name, status, description, subtask.getStartDate(), subtask.getDuration(), id);

        Assertions.assertEquals(expected1, task.serialize());
        Assertions.assertEquals(expected2, epic.serialize());
        Assertions.assertEquals(expected3, subtask.serialize());
    }

    @Test
    void deserialization() {
        int taskId = 1;
        String taskName = "TaskName";
        String taskDescription = "TaskDescription";
        TaskStatus taskStatus = TaskStatus.NEW;
        LocalDateTime startDate = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(10);

        int epicId = 2;
        String epicName = "EpicName";
        String epicDescription = "EpicDescription";
        TaskStatus epicStatus = TaskStatus.NEW;

        int subtaskId = 3;
        String subtaskName = "SubtaskName";
        String subtaskDescription = "SubtaskDescription";
        TaskStatus subtaskStatus = TaskStatus.NEW;
        int subtaskEpicId = epicId;

        String serializedTask = String.format("%s,%s,%s,%s,%s,%s,%s", taskId, TaskType.TASK, taskName, taskStatus, taskDescription, startDate, duration);
        String serializedEpic = String.format("%s,%s,%s,%s,%s,%s,%s", epicId, TaskType.EPIC, epicName, epicStatus, epicDescription, startDate, duration);
        String serializedSubtask = String.format("%s,%s,%s,%s,%s,%s,%s", subtaskId, TaskType.SUBTASK, subtaskName, subtaskStatus, subtaskDescription, startDate, duration, subtaskEpicId);

        Task task = Task.deserialize(serializedTask);
        Epic epic = Epic.deserialize(serializedEpic);
        Subtask subtask = Subtask.deserialize(serializedSubtask);

        Assertions.assertEquals(taskId, task.getId());
        Assertions.assertEquals(taskName, task.getName());
        Assertions.assertEquals(taskDescription, task.getDescription());
        Assertions.assertEquals(taskStatus, task.getStatus());

        Assertions.assertEquals(epicId, epic.getId());
        Assertions.assertEquals(epicName, epic.getName());
        Assertions.assertEquals(epicDescription, epic.getDescription());
        Assertions.assertEquals(epicStatus, epic.getStatus());

        Assertions.assertEquals(subtaskId, subtask.getId());
        Assertions.assertEquals(subtaskName, subtask.getName());
        Assertions.assertEquals(subtaskDescription, subtask.getDescription());
        Assertions.assertEquals(subtaskStatus, subtask.getStatus());
    }
}