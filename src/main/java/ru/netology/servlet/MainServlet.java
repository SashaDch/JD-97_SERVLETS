package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;

    private enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    private final String POSTS_ROOT = "/api/posts";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        controller = context.getBean("postController", PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final Method method;
            final String path = req.getRequestURI();
            try {
                 method = Method.valueOf(req.getMethod());
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if (!path.matches(POSTS_ROOT + "(/\\d+)?")) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            final long id = (path.matches(POSTS_ROOT + "/\\d+")) ?
                    Long.parseLong(path.substring(path.lastIndexOf("/") + 1)) : 0;
            switch (method) {
                case GET -> {
                    if (path.equals(POSTS_ROOT)) {
                        controller.all(resp);
                    } else {
                        controller.getById(id, resp);
                    }
                }
                case POST, PUT -> {
                    synchronized (controller) {
                        if (method == Method.PUT || id > 0L) {
                            controller.removeById(id, resp);
                        }
                        controller.save(id, req.getReader(), resp);
                    }
                    if (id <= 0L) {
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    }
                }
                case DELETE -> {
                    controller.removeById(id, resp);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

