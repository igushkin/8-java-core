import httpTaskServer.HttpTaskServer;
import kVServer.KVServer;
import manager.*;
import task.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {

        Task t = new Task(1, "task", "task", TaskStatus.NEW, LocalDateTime.MIN, 10);
        Gson gson = new Gson();
        String g = gson.toJson(t);
        System.out.println(g);

        var ts = new HttpTaskServer();
        var kvs = new KVServer();

        ts.start();
        kvs.start();
    }

}