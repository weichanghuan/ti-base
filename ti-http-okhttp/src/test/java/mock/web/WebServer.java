package mock.web;


import static spark.Spark.*;

/**
 * @Author: wch
 * @Description:
 * @Date: 2020/1/17 4:20 PM
 */
public class WebServer {
    public static void main(String[] args) {
        port(8080);

        get("/", (request, response) -> "hello world～");

        get("/hello", (request, response) -> halt(401, "Go away!"));

        post("/test", (request, response) -> "hello");
    }
}
