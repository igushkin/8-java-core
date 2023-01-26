package httpTaskServer;

import com.google.gson.Gson;

public class Response {

    private static Gson gson = new Gson();
    private int code;
    private String jsonBody;

    public Response() {
    }

    public Response(int code, String jsonBody) {
        this.code = code;
        this.jsonBody = jsonBody;
        this.gson = new Gson();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public static Response taskNotFound() {
        var response = new Response();
        response.setCode(404);
        return response;
    }

    public static Response badRequest() {
        var response = new Response();
        response.setCode(400);
        return response;
    }

    public static Response toResponse(Object obj) {
        Response response = new Response();
        response.setCode(200);
        response.setJsonBody(gson.toJson(obj));
        return response;
    }
}
