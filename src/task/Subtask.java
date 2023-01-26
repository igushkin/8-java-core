package task;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private transient Epic epic;
    private int epicId;

    public Subtask(int id, String name, String description, TaskStatus status) {
        this(id, name, description, status, null, LocalDateTime.MAX, 0);
    }

    public Subtask(Epic epic, String name, String description, TaskStatus status) {
        this(null, name, description, status, epic, LocalDateTime.MAX, 0);
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Epic epic, LocalDateTime startDate, int duration) {
        super(id, name, description, status, startDate, duration);
        this.epic = epic;
        if (epic != null)
            this.epicId = epic.getId();
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Epic epic) {
        this(id, name, description, status, epic, LocalDateTime.MAX, 0);
    }

    public Epic getEpic() {
        return this.epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
        this.epicId = epic.getId();
    }

    public int getEpicId() {
        return this.epicId;
    }

    @Override
    public String serialize() {
        return super.serialize() + "," + epic.getId();
    }

    public static Subtask deserialize(String value) {
        Task task = Task.deserialize(value);
        Subtask subtask = new Subtask(task.id, task.name, task.description, task.status);
        subtask.setStartDate(task.getStartDate());
        subtask.setDuration((int) task.getDuration().toMinutes());
        return subtask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epic.name, subtask.epic.name)
                && Objects.equals(epic.description, subtask.epic.description)
                && Objects.equals(epic.id, subtask.epic.id)
                && Objects.equals(epic.status, subtask.epic.status);
    }

    @Override
    public String toString() {
        String string = "Task.Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", duration=" + duration;

        if (this.epic != null) {
            return string += ", Epic{" +
                    "id=" + epic.id +
                    ", name='" + epic.name + '\'' +
                    ", description='" + epic.description + '\'' +
                    ", status=" + epic.status +
                    ", startDate=" + epic.getStartDate() +
                    ", duration=" + epic.duration +
                    '}' +
                    '}';
        } else {
            return string += '}';
        }
    }

    @Override
    public Object clone() {
        return new Subtask(
                this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.epic,
                this.getStartDate(),
                (int) this.getDuration().toMinutes()
        );
    }

    @Override
    public Object cloneRecursively() {
        return ((Epic) this.epic.cloneRecursively())
                .getSubtasks()
                .get(this.getId());
    }
}
