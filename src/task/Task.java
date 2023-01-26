package task;

import utility.*;

import java.time.*;
import java.util.Objects;

public class Task implements Comparable<Task>, MyCloneable {
    protected String name;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startDate;

    public Task(String name, String description, TaskStatus status) {
        this(null, name, description, status, LocalDateTime.MAX, 0);
    }

    public Task(Integer id, String name, String description, TaskStatus status, LocalDateTime startDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.startDate = startDate;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(Integer id, String name, String description, TaskStatus status) {
        this(id, name, description, status, LocalDateTime.MAX, 0);
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setDuration(int minutes) {
        if (minutes < 0) {
            throw new RuntimeException();
        }
        this.duration = Duration.ofMinutes(minutes);
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public LocalDateTime getEndTime() {
        return startDate.plusMinutes(this.duration.toMinutes());
    }

    public String serialize() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", this.id, this.getTaskType(), this.name, this.status, this.description, this.getStartDate(), this.getDuration());
    }

    public static Task deserialize(String value) {
        String[] args = value.split(",");
        int id = Integer.parseInt(args[0]);
        String name = args[2];
        TaskStatus status = TaskStatus.valueOf(args[3]);
        String description = args[4];
        LocalDateTime startDate = LocalDateTime.parse(args[5]);
        Duration duration = Duration.parse(args[6]);

        Task task = new Task(id, name, description, status, startDate, (int) duration.toMinutes());
        task.setStartDate(startDate);
        task.setDuration((int) duration.toMinutes());

        return task;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return this.id == task.id
                && Objects.equals(this.name, task.name)
                && Objects.equals(this.description, task.description)
                && Objects.equals(this.status, task.status);
    }

    @Override
    public String toString() {
        return "Task.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", duration=" + duration +
                '}';
    }

    @Override
    public int compareTo(Task o) {
        if (this == o || this.getId().equals(o.getId())) {
            return 0;
        }
        if (o.getStartDate().equals(LocalDateTime.MAX) && this.getStartDate().equals(LocalDateTime.MAX)) {
            return this.getId().compareTo(o.getId());
        }
        return this.getStartDate().compareTo(o.getStartDate());
    }

    @Override
    public Object clone() {
        return new Task(
                this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.getStartDate(),
                (int) this.getDuration().toMinutes()
        );
    }

    @Override
    public Object cloneRecursively() {
        return this.clone();
    }
}