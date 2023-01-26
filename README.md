# 8-java-core

This is part of the Java Developer course from Yandex. Module - standard Java library and its features.

The repository contains my solution that has been verified from the reviewer to the task described below.

Technical specification
===================

Back to work on the task manager. The main logic of the application has been implemented, now you can make an API for it. You will have to configure access to the manager's methods via HTTP requests.

### Working through the API logic

You need to implement an API where the endpoints will correspond to calls to the base methods of the `TaskManager` interface. Matching endpoints and methods is called mapping. This is how it should look like.

![image](https://pictures.s3.yandex.net:443/resources/S7_33-2_1649410009.png)

First, add the Gson library to the project to work with JSON. Next, create the `HttpTaskServer` class, which will listen to port 8080 and accept requests. Add the `FileBackedTaskManager` implementation to it, which can be obtained from the 'Managers` utility class. After that, you can implement mapping of requests to methods of the 'TaskManager` interface.

The API should work so that all requests along the path `/tasks/<resources>` come to the `TaskManager` interface. The path for normal tasks is `/tasks/task`, for subtasks — `/tasks/subtask`, for epics — `/tasks/epic'. It will be possible to get all tasks at once on the path `/tasks/`, and to get the history of tasks on the path `/tasks/history`.

To get data, there must be GET requests. To create and modify — POST requests. To delete — DELETE-requests. Tasks are transmitted in the request body in JSON format. The task id should be passed as a request parameter (separated by a question mark).

As a result, a separate endpoint should be created for each method of the `TaskManager` interface, which can be called over HTTP.

### How to check the endpoints

There are several ways to check the API.

1. Via Insomnia.
2. Using a browser plugin, for example, RESTED, Postman, RestClient or others. You can select and download the appropriate one by [link](https://chrome.google.com/webstore/search/REST?hl=en-US&_category=extensions ).
3. In IDEA via HTTP request templates — scratch file. Press the combination `CTRL+SHIFT+ALT+Insert' and select HTTP Request.

### Finishing up the HTTP server for storing tasks

Now the tasks are stored in files. We need to transfer them to the server. To do this, write an HTTP client. With it, we will move the storage of the manager's status from files to a separate server.

The server template is located in the repository — [https://github.com/praktikum-java/java-core-bighw-kvserver](https://github.com/praktikum-java/java-core-bighw-kvserver). Tilt it and move it to the project class [`KVServer`](https://github.com/praktikum-java/java-core-bighw-kvserver/blob/master/src/KVServer.java). In the [Main] class(http://Main.java ) see an example of how to start the server correctly. Add the same code to your project. In the example, the server starts on port 8078, if necessary, this can be changed.

Hint: how the KVServer server works

'KVServer' is a storage where data is stored according to the <key-value> principle. He can:

1. `GET /register' — register the client and issue a unique access token (authentication). This is necessary so that the storage can work with several clients at once.
2. `POST/save/<key>?API_TOKEN=` — save the contents of the request body associated with the key.
3. `GET/load/<key>?API_TOKEN=` — return stored values by key.

You need to add the implementation of the request `load()` — this is the method that is responsible for receiving data. Finish the logic of the server by comments (comments can then be removed). After that, start the server and check that getting the value by key works. For initial debugging, you can make requests without authorization using the DEBUG code.

### Writing an HTTP client

To work with the repository, you will need an HTTP client that will delegate method calls to HTTP requests. Create the `KVTaskClient` class. It will be used by the `HttpTaskManager` class, which we will write soon.

When creating `KVTaskClient`, consider the following:

* The constructor accepts the URL to the storage server and registers. During registration, a token (API\_TOKEN) is issued, which is needed when working with the server.
* The `void put(String key, String json) method` should I save the state of the task manager via the request `POST /save/<key>?API_TOKEN=`.
* The `String load(String key)` method should return the status of the task manager via the 'GET/load/<key>' request?API_TOKEN=`.

Next, check the client code in `main`. To do this, run `KVServer`, create an instance of `KVTaskClient'. Then save the value under different keys and check that the required data is returned when requested. Make sure that if you change the value, then when you call it again, it will return not the old one, but the new one.

### New implementation of the Task Manager

Now you can create a new implementation of the `TaskManager` interface — the `HttpTaskManager` class. It will inherit from `FileBackedTasksManager'.

The `HttpTaskManager` constructor will have to accept the URL to the `KVServer` server instead of the file name. Also `HttpTaskManager` creates a `KVTaskClient` from which you can get the initial state of the manager. You need to replace the stateful calls in the files with a client call.

At the end, update the static method `getDefault()` in the utility class `Managers` so that it returns `HttpTaskManager'.

### Testing

The verification code in `Main.main` has stopped working. This happened because `Managers.getDefault()` now returns a new implementation of the task manager, and it cannot work without starting the server. You need to fix it.

Add the launch of `KVServer` to `Main.main` and restart the manager usage example. Make sure everything is working and the status of the tasks is now stored on the server.

Now you can add tests for `HttpTaskManager` in the same way as they did for `FileBackedTasksManager`, the only difference is, instead of checking the recovery of the manager's state from the file, the data will be restored from the 'Kvserver' server.

Write tests for each `HttpTaskServer` endpoint. In order not to add the launch of `KVServer` and `HttpTaskServer` servers every time, you can implement a separate method in classes with tests. Mark it with the annotation `@BeforeAll` — if servers are supposed to run for all tests, or with the annotation `@beforeach` — if a separate launch is required for each test.

Hurray! Now our application is available via HTTP and can store its state on a separate server! You're doing great!
