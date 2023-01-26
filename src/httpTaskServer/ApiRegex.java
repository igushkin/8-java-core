package httpTaskServer;

import task.TaskType;

import java.util.regex.Pattern;

public class ApiRegex {
    private static final String F_REGEX_OF_GET_TASKS = "^/tasks/%s/?$";
    private static final String F_REGEX_OF_GET_TASK_BY_ID = "^/tasks/%s/\\?id=\\d+/?$";

    public static final String REGEX_OF_ALL_TASKS = String.format(F_REGEX_OF_GET_TASKS, TaskType.TASK);
    public static final String REGEX_OF_ALL_EPICS = String.format(F_REGEX_OF_GET_TASKS, TaskType.EPIC);
    public static final String REGEX_OF_ALL_SUBTASKS = String.format(F_REGEX_OF_GET_TASKS, TaskType.SUBTASK);
    public static final String REGEX_OF_TASK_WITH_ID = String.format(F_REGEX_OF_GET_TASK_BY_ID, TaskType.TASK);
    public static final String REGEX_OF_EPIC_WITH_ID = String.format(F_REGEX_OF_GET_TASK_BY_ID, TaskType.EPIC);
    public static final String REGEX_OF_SUBTASK_WITH_ID = String.format(F_REGEX_OF_GET_TASK_BY_ID, TaskType.SUBTASK);
    public static final String REGEX_OF_EPIC_SUBTASKS = "^/tasks/subtask/epic/\\?id=\\d+/?$";
    public static final String REGEX_OF_GET_HISTORY = "^/tasks/history/?$";
    public static final String REGEX_OFGET_PRIORITIZED_TASKS = "^/tasks/?$";

    public static boolean isTasksTask(String path) {
        return match(REGEX_OF_ALL_TASKS, path);
    }

    public static boolean isTasksEpic(String path) {
        return match(REGEX_OF_ALL_EPICS, path);
    }

    public static boolean isTasksSubtask(String path) {
        return match(REGEX_OF_ALL_SUBTASKS, path);
    }

    public static boolean isTasksTaskId(String path) {
        return match(REGEX_OF_TASK_WITH_ID, path);
    }

    public static boolean isTasksEpicId(String path) {
        return match(REGEX_OF_EPIC_WITH_ID, path);
    }

    public static boolean isTasksSubtaskId(String path) {
        return match(REGEX_OF_SUBTASK_WITH_ID, path);
    }

    public static boolean isTasksSubtaskEpicId(String path) {
        return match(REGEX_OF_EPIC_SUBTASKS, path);
    }

    public static boolean isTasksHistory(String path) {
        return match(REGEX_OF_GET_HISTORY, path);
    }

    public static boolean isTasks(String path) {
        return match(REGEX_OFGET_PRIORITIZED_TASKS, path);
    }

    private static boolean match(String regex, String path) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(path).find();
    }
}
