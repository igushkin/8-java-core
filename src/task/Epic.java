package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public Epic(Integer id, String name, String description, TaskStatus status, List<Subtask> subtasks) {
        super(id, name, description, status, LocalDateTime.MAX, 0);
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.EPIC;
        this.subtasks = new HashMap<>();
        if (subtasks == null) {
            subtasks = Collections.emptyList();
        }
        for (Subtask subtask : subtasks) {
            this.addSubtask(subtask);
        }
    }

    public Epic(Integer id, String name, String description, TaskStatus status) {
        this(id, name, description, status, null);
    }

    public Epic(String name, String description, TaskStatus status) {
        this(null, name, description, status);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        HashMap<Integer, Subtask> result = new HashMap<>();
        this.subtasks
                .values()
                .stream()
                .forEach(x -> result.put(x.getId(), (Subtask) x.clone()));

        return result;
    }

    private void updateStartDate() {
        LocalDateTime startDate = LocalDateTime.MAX;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartDate().isBefore(startDate)) {
                startDate = subtask.startDate;
            }
        }

        this.startDate = startDate;
    }

    private void updateDuration() {
        Duration duration = Duration.ofMinutes(0);

        for (Subtask subtask : subtasks.values()) {
            duration = duration.plusMinutes(subtask.getDuration().toMinutes());
        }

        this.duration = duration;
    }

    @Override
    public void setStartDate(LocalDateTime localDateTime) {
        //throw new RuntimeException("Дата начала выполнения задачи не может быть присвоена задаче с типом Epic .");
    }

    @Override
    public void setDuration(int minutes) {
        //throw new RuntimeException("Длительность выполнения задачи не может быть присвоена задаче с типом Epic .");
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime startDate = this.getStartDate();
        Duration duration = this.getDuration();

        if (startDate == null) {
            return null;
        }

        return startDate.plusMinutes(duration.toMinutes());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        updateStatus();
        updateStartDate();
        updateDuration();
    }

    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            throw new RuntimeException();
        } else {
            subtasks.remove(id);
            updateStatus();
            updateStartDate();
            updateDuration();
        }
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) || subtask.getId() == null) {
            throw new RuntimeException();
        } else {
            subtask = (Subtask) subtask.clone();
            subtasks.put(subtask.id, subtask);
            subtask.setEpic(this);
            updateStatus();
            updateStartDate();
            updateDuration();
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new RuntimeException();
        } else {
            subtask = (Subtask) subtask.clone();
            subtasks.put(subtask.id, subtask);
            subtask.setEpic(this);
            updateStatus();
            updateStartDate();
            updateDuration();
        }
    }

    private void updateStatus() {
        boolean isNew = subtasks.values().stream().filter(i -> i.status == TaskStatus.NEW).toArray()
                .length == subtasks.size();
        boolean isDone = subtasks.values().stream().filter(i -> i.status == TaskStatus.DONE).toArray()
                .length == subtasks.size();

        if (isNew) {
            this.status = TaskStatus.NEW;
        } else if (isDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    public static Epic deserialize(String value) {
        Task task = Task.deserialize(value);
        return new Epic(task.id, task.name, task.description, task.status);
    }

    @Override
    public void setStatus(TaskStatus status) {
        updateStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public String toString() {
        return "Task.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                ", startDate=" + startDate +
                ", duration=" + duration +
                '}';
    }

    @Override
    public Object clone() {
        return new Epic(
                this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStatus()
        );
    }

    @Override
    public Object cloneRecursively() {
        Epic epic = (Epic) this.clone();
        for (Subtask subtask : this.getSubtasks().values()) {
            epic.addSubtask((Subtask) subtask.clone());
        }
        return epic;
    }
}
