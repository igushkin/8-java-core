package httpTaskServer;

import manager.TaskManager;
import task.*;
import com.google.gson.Gson;

public class Mapper {
    private final TaskManager manager;
    private final Gson gson;

    public Mapper(TaskManager manager) {
        this.manager = manager;
        this.gson = new Gson();
    }

    public Response executeGetRequest(String path) {
        if (ApiRegex.isTasksTask(path)) {
            return Response.toResponse(manager.getAllTasks());
        } else if (ApiRegex.isTasksEpic(path)) {
            return Response.toResponse(manager.getAllEpics());
        } else if (ApiRegex.isTasksSubtask(path)) {
            return Response.toResponse(manager.getAllSubtasks());
        } else if (ApiRegex.isTasksTaskId(path)) {
            int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
            return Response.toResponse(manager.getTaskById(id));
        } else if (ApiRegex.isTasksEpicId(path)) {
            int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
            return Response.toResponse(manager.getEpicById(id));
        } else if (ApiRegex.isTasksSubtaskId(path)) {
            int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
            return Response.toResponse(manager.getSubtaskById(id));
        } else if (ApiRegex.isTasksSubtaskEpicId(path)) {
            int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
            return Response.toResponse(manager.getSubtasksByEpicId(id));
        } else if (ApiRegex.isTasksHistory(path)) {
            return Response.toResponse(manager.getHistory());
        } else if (ApiRegex.isTasks(path)) {
            return Response.toResponse(manager.getPrioritizedTasks());
        } else {
            return Response.badRequest();
        }
    }

    public Response executeDeleteRequest(String path) {
        try {
            if (ApiRegex.isTasksTask(path)) {
                manager.deleteAllTasks();
            } else if (ApiRegex.isTasksEpic(path)) {
                manager.deleteAllEpics();
            } else if (ApiRegex.isTasksSubtask(path)) {
                manager.deleteAllSubtasks();
            } else if (ApiRegex.isTasksTaskId(path)) {
                int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
                manager.deleteTaskById(id);
            } else if (ApiRegex.isTasksEpicId(path)) {
                int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
                manager.deleteEpicById(id);
            } else if (ApiRegex.isTasksSubtaskId(path)) {
                int id = Integer.parseInt(URIHelper.extractParam(path, "id"));
                manager.deleteSubtaskById(id);
            } else {
                return Response.badRequest();
            }
            return new Response(200, null);
        } catch (Exception e) {
            return Response.taskNotFound();
        }
    }

    public Response executePostRequest(String path, String body) {
        try {
            if (ApiRegex.isTasksTask(path)) {
                Task task = gson.fromJson(body, Task.class);
                manager.createTask(task);
            } else if (ApiRegex.isTasksEpic(path)) {
                Epic epic = gson.fromJson(body, Epic.class);
                manager.createEpic(epic);
            } else if (ApiRegex.isTasksSubtask(path)) {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                subtask.setEpic(manager.getEpicById(subtask.getEpicId()));
                manager.createSubtask(subtask);
            } else {
                return Response.badRequest();
            }
            return new Response(200, null);
        } catch (Exception e) {
            return Response.badRequest();
        }
    }
}