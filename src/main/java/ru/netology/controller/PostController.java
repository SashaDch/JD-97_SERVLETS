package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicLong;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;
    private final AtomicLong counter = new AtomicLong();

    public PostController(PostService service) {
        this.service = service;
    }

    public void all(HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var data = service.all();
        final var gson = new Gson();
        response.getWriter().print(gson.toJson(data));
    }

    public void getById(long id, HttpServletResponse response) throws IOException, NotFoundException {
        response.setContentType(APPLICATION_JSON);
        final var data = service.getById(id);
        final var gson = new Gson();
        response.getWriter().print(gson.toJson(data));
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        final var post = gson.fromJson(body, Post.class);
        if (post.getId() == 0L) {
            post.setId(counter.incrementAndGet());
        }
        final var data = service.save(post);
        response.getWriter().print(gson.toJson(data));
    }

    public void removeById(long id, HttpServletResponse response) throws NotFoundException {
        service.removeById(id);
    }
}
