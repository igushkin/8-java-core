package httpTaskServer;

public class URIHelper {

    public static String extractParam(String path, String paramKey) {
        String queryPart = path.split("\\?")[1];
        String[] params = queryPart.split("&");

        for (String param : params) {
            String key = param.split("=")[0];
            String value = param.split("=")[1];
            if (key.equals(paramKey)) {
                return value;
            }
        }
        return null;
    }
}
