package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PostRepository {

    private final Map<Long, Post> posts = new ConcurrentHashMap<>();

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return posts.containsKey(id) ? Optional.of(posts.get(id)) : Optional.empty();
  }

  public Post save(Post post) {
      posts.put(post.getId(), post);
    return post;
  }

  public void removeById(long id) {
      posts.remove(id);
  }
}
