package exercise;

import java.net.URI;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import exercise.model.Post;

@SpringBootApplication
@RestController
public class Application {
    // Хранилище добавленных постов
    private List<Post> posts = Data.getPosts();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // BEGIN
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getPosts(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        var result = posts
            .stream()
            .skip(page - 1)
            .limit(limit)
            .toList();

        return ResponseEntity
            .ok()
            .header("X-Total-Count", Integer.toString(posts.size()))
            .body(result);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) {
        var result = posts
            .stream()
            .filter(post -> post.getId().equals(id))
            .findFirst();

        return ResponseEntity.of(result);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> addPost(@RequestBody Post post) {
        posts.add(post);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Post post) {
        var optionalOldPost = posts
            .stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();

        if (optionalOldPost.isPresent()) {
            var oldPost = optionalOldPost.orElseThrow();
            oldPost.setId(post.getId());
            oldPost.setTitle(post.getTitle());
            oldPost.setBody(post.getBody());

            return ResponseEntity
                .ok(post);
        } else {
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(post);
        }
    }
    // END

    @DeleteMapping("/posts/{id}")
    public void destroy(@PathVariable String id) {
        posts.removeIf(p -> p.getId().equals(id));
    }
}
