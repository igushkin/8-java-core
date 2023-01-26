package manager;

import task.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;

    Set<Task> prioritizedTasks;

    protected HistoryManager historyManager;

    private static int id;

    static {
        id = 0;
    }

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>();
    }


    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.epics.values().stream()
                .flatMap(i -> i.getSubtasks().values().stream())
                .collect(Collectors.toList())
        );
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        addToViewHistory(task);
        return (Task) task.clone();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        addToViewHistory(epic);
        return (Epic) epic.cloneRecursively();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = findSubtask(id);
        if (subtask == null) {
            return null;
        }
        addToViewHistory(subtask);
        return (Subtask) subtask.cloneRecursively();
    }

    protected Subtask findSubtask(int id) {
        Subtask subtask = getAllSubtasks().stream()
                .filter(i -> i.getId() == id)
                .findAny()
                .orElse(null);
        return subtask;
    }

    private void addToViewHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager
                .getHistory()
                .stream()
                .map(x -> (Task) x.clone())
                .collect(Collectors.toList());
    }

    @Override
    public void createTask(Task task) {
        setIdIfNull(task);
        task = (Task) task.clone();
        if (tasks.containsKey(task.getId()) ||
                this.isThereTimeIntersection(task)) {
            throw new RuntimeException();
        } else {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        setIdIfNull(epic);
        epic = (Epic) epic.cloneRecursively();
        if (epics.containsKey(epic.getId())) {
            throw new RuntimeException();
        } else {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        setIdIfNull(subtask);
        if (findSubtask(subtask.getId()) != null ||
                subtask.getEpic() == null ||
                !epics.containsKey(subtask.getEpic().getId()) ||
                this.isThereTimeIntersection(subtask)
        ) {
            throw new RuntimeException();
        } else {
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.addSubtask(subtask);
            prioritizedTasks.add((Subtask) subtask.clone());
        }
    }

    private void setIdIfNull(Task task) {
        if (task.getId() == null) {
            task.setId(generateId());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId()) ||
                this.isThereTimeIntersection(task)) {
            throw new RuntimeException();
        } else {
            task = (Task) task.clone();
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (!epics.containsKey(newEpic.getId())) {
            throw new RuntimeException();
        } else {
            Epic epic = epics.get(newEpic.getId());
            epic.setName(newEpic.getName());
            epic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getEpic() == null ||
                this.isThereTimeIntersection(subtask)) {
            throw new RuntimeException();
        } else {
            Epic epic = epics.get(subtask.getEpic().getId());
            prioritizedTasks.remove(subtask);
            epic.updateSubtask(subtask);
            prioritizedTasks.add((Subtask) subtask.clone());
        }
    }

    @Override
    public void deleteAllTasks() {
        ArrayList<Integer> ids = new ArrayList(tasks.keySet());
        for (int id : ids) {
            deleteTaskById(id);
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        ArrayList<Integer> ids = new ArrayList(epics.keySet());
        for (int id : ids) {
            deleteEpicById(id);
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> ids = new ArrayList(epic.getSubtasks().keySet());
            for (int id : ids) {
                deleteSubtaskById(id);
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new RuntimeException();
        } else {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new RuntimeException();
        } else {
            ArrayList<Integer> subtaskIds = new ArrayList<>(getSubtasksByEpicId(id).keySet());
            for (int subtaskId : subtaskIds) {
                deleteSubtaskById(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = findSubtask(id);
        if (subtask == null) {
            throw new RuntimeException();
        } else {
            Epic epic = subtask.getEpic();
            prioritizedTasks.remove(subtask);
            epic.deleteSubtaskById(id);
            historyManager.remove(id);
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksByEpicId(int id) {
        if (!epics.containsKey(id)) {
            throw new RuntimeException();
        } else {
            return epics.get(id).getSubtasks();
        }
    }

    private boolean isThereTimeIntersection(Task task1, Task task2) {

        if (task1.getId().equals(task2.getId())) {
            return false;
        }

        if (task1.getStartDate().equals(LocalDateTime.MAX) || task2.getStartDate().equals(LocalDateTime.MAX)) {
            return false;
        }

        if (task1.getStartDate().equals(task2.getStartDate())) {
            return true;
        }

        if (task2.getStartDate().isBefore(task1.getStartDate()) ||
                task2.getStartDate().equals(task1.getStartDate())) {
            return task2.getEndTime().isAfter(task1.getStartDate());
        }

        return task2.getStartDate().isAfter(task1.getStartDate()) &&
                task2.getStartDate().isBefore(task1.getEndTime());

    }

    @Override
    public boolean isThereTimeIntersection(Task taskToAdd) {
        if (taskToAdd.getTaskType().equals(TaskType.EPIC)) {
            throw new RuntimeException();
        }

        List<Task> tasks = Stream.of(this.getAllTasks(), this.getAllSubtasks())
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());

        for (Task task : tasks) {
            if (isThereTimeIntersection(taskToAdd, task)) {
                return true;
            }
        }
        return false;
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> list = new ArrayList<>();
        Iterator<Task> iterator = prioritizedTasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();
            list.add((Task) task.clone());
        }

        return list;
    }

    private static int generateId() {
        return id++;
    }

    public static void addTasks(TaskManager manager) {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);

        Epic epic1 = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание 2", TaskStatus.NEW);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.createSubtask(new Subtask(epic1, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW));
        manager.createSubtask(new Subtask(epic1, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW));
        manager.createSubtask(new Subtask(epic1, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW));
    }
}

